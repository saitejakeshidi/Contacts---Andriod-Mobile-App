package com.uncc.inclass12;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;

public class MyAdpater extends RecyclerView.Adapter<MyAdpater.ViewHolder> {

    public static InteractWithMainActivity interact;

    private ArrayList<Contact> contactArrayList;

    static String TAG = "demo";

    private Context context;

    public MyAdpater(ArrayList<Contact> contactArrayList, Context context) {
        this.contactArrayList = contactArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        interact = (InteractWithMainActivity) context;

        final Contact contactObj = contactArrayList.get(position);
        holder.nameTxtView.setText(contactObj.getName());
        holder.phoneNoTxtView.setText(contactObj.getPhoneNumber());
        holder.emailTxtView.setText(contactObj.getEmail());
        if (contactObj.getContactImage() != null) {
            new DownloadImageTask((ImageView) holder.contactImageView)
                    .execute(contactObj.getContactImage());

        } else {
            holder.contactImageView.setImageResource(R.drawable.iconfinder_contact_card);
        }


        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                interact.deleteItem(position);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxtView, emailTxtView, phoneNoTxtView;
        private ImageView contactImageView;
        public ConstraintLayout constraintLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxtView = itemView.findViewById(R.id.nameTxtView);
            emailTxtView = itemView.findViewById(R.id.emailTxtView);
            phoneNoTxtView = itemView.findViewById(R.id.phoneNoTxtView);
            contactImageView = itemView.findViewById(R.id.contactImageView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

        }
    }

    public interface InteractWithMainActivity {
        void deleteItem(int position);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

