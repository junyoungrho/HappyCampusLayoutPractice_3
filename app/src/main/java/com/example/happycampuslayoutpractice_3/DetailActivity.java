package com.example.happycampuslayoutpractice_3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    DetailActivityAdapter myAdapter;
    RecyclerView recyclerView;

    PostItem items[];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        recyclerView = findViewById(R.id.detail_activity_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        myAdapter = new DetailActivityAdapter(new PostItem[]{});
        recyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("joon", "position은 " + position + "name is " + items[position].name );
            }
        });

        Request request = new Request.Builder()
                .url("http://172.30.1.56:8000/category/"+getIntent().getIntExtra("id", 0)+"/post_list")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("joon", "네트워크 연결을 확인해주세요");
                Log.d("joon", "" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
            Log.d("artcow", "body : " + body);
                try {
                    JSONArray ojb = new JSONArray(body);
                    items = new PostItem[ojb.length()];
                    // =====
//
//                    for (int i = 0; i < ojb.length(); i++){
//                        JSONObject o = (JSONObject) ojb.get(i);
//                        Log.d("Joon", i + "index: " + o.getString("name") + "," + o.getString("img_path"));
//                        item[i] = new Item(o.getString("name"), "", o.getString("img_path"));
//
//                    }

                    // =====
                    for (int i = 0; i < ojb.length(); i ++) {

                        JSONObject o = (JSONObject)ojb.get(i);
                        Log.d("artcow", "o : " + o);

                        items[i] = new PostItem(o.getInt("id"), o.getString("name"), o.getString("img_path"));
                    }

//                    myAdapter = new MyAdapter(item);

                    Log.d("joon", "111111");
                    myAdapter.items = items;
                    myAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(myAdapter);
                    Log.d("joon", "222222");


                } catch (Throwable e){
                    Log.d("joon", "Could not parse malformed JSON" + body);
                }

            }
        });


        // MainActivity.Item i = new MainActivity.Item()

    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    class PostItem {
        int id;
        String name, imagePath;

        // {"id":9,
        // "name":"우리나라 노인복지 서비스중 가장 필요한 부분은 무엇이며",
        // "img_path":"https:\/\/image4.happycampus.com\/Production\/thumb212\/2020\/06\/10\/data24598051-0001.jpg"}
        PostItem(int id, String n, String i) {
            this.id = id;
            name = n;
            imagePath = i;
        }

    }

    class DetailActivityAdapter extends RecyclerView.Adapter<DetailActivityAdapter.ViewHolder> {


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title_text_view;
//            ImageView imageView;

            ViewHolder(View v) {
                super(v);
                title_text_view = v.findViewById(R.id.post_title);
//                tag_text_view = v.findViewById(R.id.tag_text);
//                imageView = v.findViewById(R.id.image_view);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            PostItem item = DetailActivity.this.items[pos];
                            if(mListener != null){
                                mListener.onItemClick(v, pos);
                            }
                        }
                    }
                });
            }

        }

        private OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mListener = listener;
        }

        private PostItem[] items;

        DetailActivityAdapter(PostItem[] myDataset) {
            items = myDataset;
        }

        @NonNull
        @Override
        public DetailActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailactivity_recycler_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull DetailActivityAdapter.ViewHolder holder, int position) {
            holder.title_text_view.setText(items[position].name);
//            Glide.with(getApplicationContext()).load(items[position].imagePath).into(holder.imageView);

        }

        @Override
        public int getItemCount() { return items.length; }

    }
}
