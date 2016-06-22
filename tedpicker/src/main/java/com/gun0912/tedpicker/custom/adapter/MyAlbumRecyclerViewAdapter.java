package com.gun0912.tedpicker.custom.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpicker.AlbumFragment;
import com.gun0912.tedpicker.R;
import com.gun0912.tedpicker.models.GalleryPhotoAlbum;

import java.util.List;


public class MyAlbumRecyclerViewAdapter extends RecyclerView.Adapter<MyAlbumRecyclerViewAdapter.ViewHolder> {

    private final List<GalleryPhotoAlbum> mValues;
    private final AlbumFragment.OnListFragmentInteractionListener mListener;
    private final Context context;

    public MyAlbumRecyclerViewAdapter(Context context, List<GalleryPhotoAlbum> items, AlbumFragment.OnListFragmentInteractionListener listener) {
        this.context = context;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Uri mUri = Uri.parse(holder.mItem.getData());
        if (holder.uri == null || !holder.uri.equals(mUri)) {
            Glide.with(context)
                    .load(mUri.toString())
                    .thumbnail(0.1f)
                            //.fit()
                    .dontAnimate()
                            //   .override(holder.mThumbnail.getWidth(), holder.mThumbnail.getWidth())
                            //  .override(holder.root.getWidth(), holder.root.getWidth())
                    .centerCrop()
                    .placeholder(R.drawable.place_holder_gallery)
                    .error(R.drawable.no_image)

                    .into(holder.mThumbnail);
            holder.uri = mUri;


        }
        holder.mContentView.setText(mValues.get(position).getBucketName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mThumbnail;
        public final TextView mContentView;
        public GalleryPhotoAlbum mItem;
        Uri uri;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail_image);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
