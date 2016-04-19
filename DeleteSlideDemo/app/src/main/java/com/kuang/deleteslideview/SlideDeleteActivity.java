package com.kuang.deleteslideview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuang.deleteslideview.view.SlideDeleteView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SlideDeleteActivity extends Activity {

    private ListView  mLv_slidedelete;
    private MyAdapter mAdapter;
    private List<String> datas      = new ArrayList<String>();

    private int          selectItem = -1;
    private Set<Integer> selectItems = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initListener();
    }

    private void initListener() {

        mLv_slidedelete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(SlideDeleteActivity.this, "ItemClick", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {

        for (int i= 0;i<100;i++){

            datas.add("条目"+i);
        }

        mAdapter = new MyAdapter();
        mLv_slidedelete.setAdapter(mAdapter);
    }

    private void initView() {
        setContentView(R.layout.activity_slidedelete);

        mLv_slidedelete = (ListView) findViewById(R.id.lv_slidedelete_activity);
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return datas.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHold viewHold;

            if (convertView == null){

                convertView = View.inflate(SlideDeleteActivity.this,R.layout.item_lv_slidedeleteactivity,null);

                viewHold = new ViewHold();

                convertView.setTag(viewHold);


                viewHold.sdv = (SlideDeleteView) convertView.findViewById(R.id.sdv_item_lv_activity);

                viewHold.tv_content = (TextView) convertView.findViewById(R.id.tv_content_slideview);

                viewHold.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete_slideview);

            }else {

                viewHold = (ViewHold) convertView.getTag();
            }


            //取值赋值

            viewHold.tv_content.setText(datas.get(position));

            viewHold.tv_delete.setText("delete");

            //viewHold.sdv.startScrollAnimation(position == selectItem);
            //viewHold.sdv.startScrollAnimation(selectItems.contains(position));

            if (selectItems.contains(position)){

                viewHold.sdv.showDelete();

            }else {

                viewHold.sdv.closeDelete();
            }
            viewHold.sdv.setOnViewSlideFinishListener(new SlideDeleteView.OnViewSlideFinishListener() {

                @Override
                public void setDeleteIsShow(boolean deleteIsShow) {
                    //delete完全显示或完全不显示
                    if (deleteIsShow){

                        selectItems.add(position);
                    }else {

                        selectItems.remove(position);
                    }
                    MyAdapter.this.notifyDataSetChanged();

                }
            });

            viewHold.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("tagtag","tv_delete:"+position);

                    datas.remove(position);
                    selectItems.remove(position);
                    MyAdapter.this.notifyDataSetChanged();

                    viewHold.sdv.closeDelete();
                }
            });

            return convertView;
        }

        class ViewHold{

            SlideDeleteView sdv;

            TextView tv_content;

            TextView tv_delete;
        }
    }
}