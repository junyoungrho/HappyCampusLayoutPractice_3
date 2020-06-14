package com.example.happycampuslayoutpractice_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    MyAdapter myAdapter;
    RecyclerView recyclerView;


    Item item[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);


        myAdapter = new MyAdapter(new Item[]{});
        recyclerView.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent i = new Intent(MainActivity.this,DetailActivity.class);
                i.putExtra("title",item[position].title);
                i.putExtra("id", item[position].id);
                startActivity(i);
            }
        });


        Request request = new Request.Builder()
                .url("http://172.30.1.56:8000/category")
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
                try {
                    JSONArray ojb = new JSONArray(body);
                    item = new Item[ojb.length()];
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
//                                Log.d("artcow", "category_name : " + j.getString("category_name"));
//                                Log.d("artcow", "tags : " + j.getJSONArray("tags"));
//                                Log.d("artcow", "main_image_path : " + j.getString("main_image_path"));
                            String tag = "";
                            JSONArray tags = o.getJSONArray("tags");
                            Log.d("artcow", "raw :" + tags);
                            for (int j = 0; j < tags.length(); j ++) {
                                Log.d("artcow", "태그... : " +  tags.get(j));
                                tag += tags.get(j).toString() + "";
                            }
                            Log.d("artcow", "tags? : " + tag);
                             item[i] = new Item(o.getInt("id"),  o.getString("category_name"), tag, o.getString("main_image_path"));
//                            }
//                            JSONObject o = (JSONObject) ojb.get(i);
//                            Log.d("Joon", i + "index: " + o.getString("name") + "," + o.getString("img_path"));
                        }
                        // [{"category_name":"사회복지사","tags":["# 사회복지개론","# 인간행동사회","# 현장실습"],"main_image_path":"https://t1.daumcdn.net/cfile/tistory/1828A04B4FE02A6E07"},{"category_name":"보육교사","tags":["# 보육학개론","# 영유아발달","# 보육과정"],"main_image_path":"https://t1.daumcdn.net/cfile/tistory/1828A04B4FE02A6E07"},{"category_name":"평생교육사","tags":["# 평생교육론","# 평생교육방법론","# 교육실습"],"main_image_path":"https://t1.daumcdn.net/cfile/tistory/1828A04B4FE02A6E07"},{"category_name":"간호사","tags":["# 병원","# 지역","# 부서","# 성모병원","# 백병원","# 의료원"],"main_image_path":"https://t1.daumcdn.net/cfile/tistory/1828A04B4FE02A6E07"}]

//                    myAdapter = new MyAdapter(item);

                    myAdapter.items = item;
                    myAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(myAdapter);

                } catch (Throwable e){
                    Log.d("joon", "Could not parse malformed JSON" + body);
                }

            }
        });


    }

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title_text_view, tag_text_view;
            ImageView imageView;

            ViewHolder(View v) {
                super(v);
                title_text_view = v.findViewById(R.id.title_text_view);
                tag_text_view = v.findViewById(R.id.tag_text);
                imageView = v.findViewById(R.id.image_view);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
                            Item item = MainActivity.this.item[pos];
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

        private Item[] items;

        MyAdapter(Item[] myDataset) {
            items = myDataset;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.title_text_view.setText(items[position].title);
            holder.tag_text_view.setText(items[position].summary);
            Glide.with(getApplicationContext()).load(items[position].imagePath).into(holder.imageView);

        }

        @Override
        public int getItemCount() { return items.length; }

    }


    class Item {
        int id;
        String title, summary, imagePath;

        Item(int id, String t, String s, String i) {
            this.id = id;
            title = t;
            summary = s;
            imagePath = i;
        }

    }
}
