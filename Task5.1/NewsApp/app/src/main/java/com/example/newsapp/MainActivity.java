package com.example.newsapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // === Models ===
    public static class News {
        private final String title;
        private final String description;
        private final int imageResId;

        public News(String title, String description, int imageResId) {
            this.title = title;
            this.description = description;
            this.imageResId = imageResId;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getImageResId() { return imageResId; }
    }

    // === Index Page Fragment ===
    public static class NewsFragment extends Fragment {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_news, container, false);

            // Initial Top News
            RecyclerView topStoriesRecycler = view.findViewById(R.id.topStoriesRecycler);
            topStoriesRecycler.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));  // Horizontal layout

            // Slide Button
            ImageButton btnLeft = view.findViewById(R.id.btnLeft);
            ImageButton btnRight = view.findViewById(R.id.btnRight);

            btnLeft.setOnClickListener(v -> topStoriesRecycler.smoothScrollBy(-300, 0));
            btnRight.setOnClickListener(v -> topStoriesRecycler.smoothScrollBy(300, 0));

            // Initial Related News
            RecyclerView newsRecycler = view.findViewById(R.id.newsRecycler);
            newsRecycler.setLayoutManager(
                    new GridLayoutManager(getContext(), 2));
                    // new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));  // Vertical layout

            ArrayList<News> topStoriesList = getDummyNews();
            ArrayList<News> newsList = getDummyNews();


            NewsAdapter topAdapter = new NewsAdapter(topStoriesList, this::openDetail, true);
            NewsAdapter newsAdapter = new NewsAdapter(newsList, this::openDetail, false);

            topStoriesRecycler.setAdapter(topAdapter);
            newsRecycler.setAdapter(newsAdapter);

            return view;
        }

        private void openDetail(News news) {
            Bundle bundle = new Bundle();
            bundle.putString("title", news.getTitle());
            bundle.putString("description", news.getDescription());
            bundle.putInt("image", news.getImageResId());

            NewsDetailFragment detailFragment = new NewsDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }

        // Create Example data
        private ArrayList<News> getDummyNews() {
            ArrayList<News> list = new ArrayList<>();
            list.add(new News("Global Tech Conference 2025 Kicks Off in Berlin", "The world's largest technology summit opened today in Berlin, featuring keynote speeches from industry leaders and showcasing cutting-edge innovations in AI, robotics, and green energy.", R.drawable.bg1));
            list.add(new News("NASA Confirms Water Found on Mars' Surface", "NASA scientists announced a groundbreaking discovery of trace amounts of water on the surface of Mars, raising new hopes for future colonization missions and extraterrestrial life research.", R.drawable.bg2));
            list.add(new News("Electric Vehicles Outsell Gas Cars in Europe for First Time", "For the first time in history, electric vehicles have surpassed gasoline-powered car sales in Europe, signaling a major shift toward sustainable transportation across the continent.", R.drawable.bg3));
            list.add(new News("Apple Unveils VisionOS Glasses at Spring Event", "Apple introduced its long-awaited augmented reality glasses, powered by VisionOS, promising seamless integration with iPhones and a revolutionary AR experience for daily tasks and entertainment.", R.drawable.bg4));
            list.add(new News("UN Launches Global Education Recovery Plan Post-COVID", "The United Nations has initiated a $5 billion education recovery plan to help students around the globe who were affected by the pandemic, aiming to close the widening learning gap.", R.drawable.bg5));
            return list;
        }

    }

    // === Detail Page Fragment ===
    public static class NewsDetailFragment extends Fragment {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_news_detail, container, false);

            ImageView imageView = view.findViewById(R.id.newsDetailImage);
            TextView titleView = view.findViewById(R.id.newsDetailTitle);
            TextView descView = view.findViewById(R.id.newsDetailDesc);
            RecyclerView relatedRecycler = view.findViewById(R.id.relatedNewsRecycler);

            Bundle args = getArguments();
            if (args != null) {
                imageView.setImageResource(args.getInt("image"));
                titleView.setText(args.getString("title"));
                descView.setText(args.getString("description"));
            }

            ArrayList<News> relatedList = new ArrayList<>();
            relatedList.add(new News("9news", "This is a description for the news item. It may be really long long long long long.", R.drawable.plus1));
            relatedList.add(new News("abc news", "This is a description for the news item. It may be really long long long long long. This is a description for the news item. It may be really long long long long long.", R.drawable.plus2));

            relatedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            relatedRecycler.setAdapter(new RelatedNewsAdapter(relatedList));

            return view;
        }
    }

    // === Index News Adapter ===
    public static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {


        public interface OnNewsClickListener {
            void onClick(MainActivity.News news);
        }

        private final ArrayList<MainActivity.News> list;
        private final OnNewsClickListener listener;
        private final boolean isTopStory;

        public NewsAdapter(ArrayList<MainActivity.News> list, OnNewsClickListener listener, boolean isTopStory) {
            this.list = list;
            this.listener = listener;
            this.isTopStory = isTopStory;
        }

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = isTopStory ? R.layout.item_top_story : R.layout.item_news;
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
            MainActivity.News news = list.get(position);
            holder.title.setText(news.getTitle());
            if (holder.desc != null) {
                holder.desc.setText(news.getDescription());
            }
            holder.image.setImageResource(news.getImageResId());
            holder.itemView.setOnClickListener(v -> listener.onClick(news));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class NewsViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            TextView desc;
            public NewsViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.newsImage);
                title = itemView.findViewById(R.id.newsTitle);
                desc = itemView.findViewById(R.id.newsDesc);
            }
        }
    }

    // === Related News Adapter ===
    public static class RelatedNewsAdapter extends RecyclerView.Adapter<RelatedNewsAdapter.RelatedViewHolder> {

        private final ArrayList<News> list;

        public RelatedNewsAdapter(ArrayList<News> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public RelatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_related_news, parent, false);
            return new RelatedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RelatedViewHolder holder, int position) {
            News news = list.get(position);
            holder.title.setText(news.getTitle());
            holder.desc.setText(news.getDescription());
            holder.image.setImageResource(news.getImageResId());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class RelatedViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title, desc;

            public RelatedViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.relatedImage);
                title = itemView.findViewById(R.id.relatedTitle);
                desc = itemView.findViewById(R.id.relatedDesc);
            }
        }
    }

    // === MainActivity Main Entry ===
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new NewsFragment())
                    .commit();
        }
    }
}

