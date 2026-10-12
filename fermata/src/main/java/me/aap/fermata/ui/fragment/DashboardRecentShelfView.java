package me.aap.fermata.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.aap.fermata.R;
import me.aap.fermata.media.lib.MediaLib.Item;
import me.aap.fermata.media.lib.MediaLib.PlayableItem;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.fermata.ui.view.MediaHubStyle;

/**
 * A real-library recent or favorites shelf. No sample media or extra playback owner is introduced.
 */
public final class DashboardRecentShelfView extends LinearLayout {
	private final TextView heading;
	private final TextView empty;
	private final RecyclerView row;
	private final RecentAdapter adapter = new RecentAdapter();
	private final List<PlayableItem> items = new ArrayList<>();
	@Nullable
	private MainActivityDelegate activity;
	private int generation;
	private boolean favorites;

	public DashboardRecentShelfView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		heading = new TextView(context);
		heading.setTextSize(20);
		heading.setTypeface(null, android.graphics.Typeface.BOLD);
		heading.setTextColor(MediaHubStyle.color(context, android.R.attr.textColorPrimary, 0xFFF2F5FB));
		heading.setGravity(Gravity.CENTER_VERTICAL);
		heading.setMinimumHeight(dp(48));
		heading.setFocusable(true);
		heading.setBackgroundResource(me.aap.utils.R.drawable.focusable_shape_transparent);
		heading.setOnClickListener(v -> openRecent());
		addView(heading, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		empty = new TextView(context);
		empty.setTextSize(14);
		empty.setTextColor(MediaHubStyle.color(context, android.R.attr.textColorSecondary, 0xFFB2C1D8));
		empty.setPadding(dp(16), dp(20), dp(16), dp(20));
		empty.setBackgroundResource(R.drawable.media_item_bg);
		addView(empty, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		row = new RecyclerView(context);
		row.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
		row.setAdapter(adapter);
		row.setClipToPadding(false);
		addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, dp(160)));
	}

	public void bind(MainActivityDelegate activity) {
		bind(activity, favorites);
	}

	public void bind(MainActivityDelegate activity, boolean favorites) {
		this.activity = activity;
		this.favorites = favorites;
		Context localized = activity.getLocalizedContext(getContext());
		heading.setText(localized.getString(favorites ? R.string.dashboard_favorites_shelf :
				R.string.dashboard_recent_shelf) + "  ›");
		heading.setContentDescription(localized.getString(favorites ? R.string.favorites :
				R.string.recent));
		empty.setText(localized.getString(favorites ? R.string.dashboard_favorites_empty :
				R.string.dashboard_recent_empty));
		float fontScale = Math.max(1F, Math.min(2F, getResources().getConfiguration().fontScale));
		row.getLayoutParams().height = dp(160 + Math.round(60 * (fontScale - 1F)));
		row.requestLayout();
		int request = ++generation;
		(favorites ? activity.getLib().getFavorites() : activity.getLib().getRecent())
				.getChildren().main().onCompletion((children, error) -> {
			if (request != generation) return;
			items.clear();
			if ((error == null) && (children != null)) {
				for (Item item : children) {
					if (item instanceof PlayableItem playable) items.add(playable);
					if (items.size() == 10) break;
				}
			}
			adapter.notifyDataSetChanged();
			empty.setVisibility(items.isEmpty() ? VISIBLE : GONE);
			row.setVisibility(items.isEmpty() ? GONE : VISIBLE);
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (activity != null) bind(activity, favorites);
	}

	@Override
	protected void onDetachedFromWindow() {
		generation++;
		super.onDetachedFromWindow();
	}

	private void openRecent() {
		if (activity == null) return;
		activity.setActiveNavItemId(R.id.dashboard_fragment);
		activity.showFragmentWhenReady(favorites ? R.id.favorites_fragment : R.id.recent_fragment);
	}

	private int dp(int value) {
		return Math.round(value * getResources().getDisplayMetrics().density);
	}

	private final class RecentAdapter extends RecyclerView.Adapter<RecentHolder> {
		@NonNull
		@Override
		public RecentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new RecentHolder(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.dashboard_recent_tile, parent, false));
		}

		@Override
		public void onBindViewHolder(@NonNull RecentHolder holder, int position) {
			PlayableItem item = items.get(position);
			holder.item = item;
			int request = generation;
			holder.art.setImageResource(item.getIcon());
			holder.art.setImageTintList(ColorStateList.valueOf(0xFF76DFC5));
			holder.title.setText(item.getName());
			holder.subtitle.setText(item.getParent() == null ? "" : item.getParent().getName());
			holder.itemView.getLayoutParams().height = row.getLayoutParams().height;
			holder.itemView.setOnClickListener(v -> {
				if ((activity == null) || (holder.item != item)) return;
				DashboardPlayableNavigator.playAndGoToPlayable(activity, item);
			});
			item.getMediaDescription().main().onSuccess(description -> {
				if ((request != generation) || (holder.item != item)) return;
				if (description.getTitle() != null) holder.title.setText(description.getTitle());
				if (description.getSubtitle() != null) holder.subtitle.setText(description.getSubtitle());
				Bitmap bitmap = description.getIconBitmap();
				if (bitmap != null) setBitmap(holder, bitmap);
				else if (description.getIconUri() != null) {
					item.getLib().getBitmap(description.getIconUri().toString(), true, true)
							.main().onSuccess(art -> {
								if ((request == generation) && (holder.item == item) && (art != null))
									setBitmap(holder, art);
							});
				}
			});
		}

		private void setBitmap(RecentHolder holder, Bitmap bitmap) {
			holder.art.setImageTintList(null);
			holder.art.setImageBitmap(bitmap);
		}

		@Override
		public void onViewRecycled(@NonNull RecentHolder holder) {
			holder.item = null;
			holder.art.setImageDrawable(null);
			holder.itemView.setOnClickListener(null);
		}

		@Override
		public int getItemCount() {
			return items.size();
		}
	}

	private static final class RecentHolder extends RecyclerView.ViewHolder {
		final ImageView art;
		final TextView title;
		final TextView subtitle;
		PlayableItem item;

		RecentHolder(View view) {
			super(view);
			art = view.findViewById(R.id.dashboard_recent_art);
			title = view.findViewById(R.id.dashboard_recent_name);
			subtitle = view.findViewById(R.id.dashboard_recent_description);
		}
	}
}
