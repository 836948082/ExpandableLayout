package com.runtai.expandablelayout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runtai.expandablelayoutlibrary.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {

    private SimpleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SimpleAdapter(getContext(), recyclerView, creatDatas());
        recyclerView.setAdapter(adapter);
        adapter.setSelectPosition(0);//默认选中

        return rootView;
    }

    private static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

        private static final int UNSELECTED = -1;
        private boolean isfirst = true; //首次界面载入打开，后面防止上下滑动后item重绘时再次打开
        private int defaultSelection = -1;

        public void setSelectPosition(int position) {
            if (!(position < 0 || position > 100)) {
                defaultSelection = position;
                notifyDataSetChanged();
            }
        }

        private RecyclerView recyclerView;
        private Context context;
        private List<DataBean> all_data;
        private int selectedItem = UNSELECTED;

        public SimpleAdapter(Context context, RecyclerView recyclerView, List<DataBean> all_data) {
            this.context = context;
            this.recyclerView = recyclerView;
            this.all_data = all_data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return all_data == null ? 0 : all_data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ExpandableLayout expandableLayout;
            private RelativeLayout relativelayout;
            private TextView expandButton;
            private RecyclerView recyclerview;
            private ImageView image;
            private int position;
            private SimpleTwoAdapter adapter_two;

            public ViewHolder(View itemView) {
                super(itemView);

                expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);
                expandableLayout.setInterpolator(new OvershootInterpolator());
                relativelayout = (RelativeLayout) itemView.findViewById(R.id.relativelayout);
                expandButton = (TextView) itemView.findViewById(R.id.expand_button);
                image = (ImageView) itemView.findViewById(R.id.image);

                recyclerview = (RecyclerView) itemView.findViewById(R.id.recyclerview);
                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerview.setLayoutManager(llm);
                recyclerview.setAdapter(adapter_two = new SimpleTwoAdapter(all_data.get(position).getList()));

                relativelayout.setOnClickListener(this);
            }

            public void bind(int position) {
                this.position = position;
                expandButton.setText(all_data.get(position).getGroup());

                if (position == defaultSelection) {//重绘复用控件时，展开指定的改控件
                    if (isfirst) {
                        relativelayout.setSelected(true);
                        expandableLayout.expand();

                        //动画旋转打开效果
                        {
                            selectedItem = position;
                            Log.e("展开", "展开" + isfirst);
                            // 初始化需要加载的动画资源
                            Animation open_anim = AnimationUtils.loadAnimation(context, R.anim.open_anim);
                            open_anim.setFillAfter(true);
                            // 将TextView执行Animation动画
                            image.startAnimation(open_anim);
                        }
                    }
                } else if (selectedItem == position) {
                    relativelayout.setSelected(true);
                    expandableLayout.expand();

                    //动画旋转打开效果
                    {
                        selectedItem = position;
                        Log.e("展开", "展开" + "点击");
                        // 初始化需要加载的动画资源
                        Animation open_anim = AnimationUtils.loadAnimation(context, R.anim.open_anim);
                        open_anim.setFillAfter(true);
                        // 将TextView执行Animation动画
                        image.startAnimation(open_anim);

                        adapter_two.setData(all_data.get(position).getList(), position);
                    }
                } else {//重绘复用控件时，不展开
                    relativelayout.setSelected(false);
                    expandableLayout.collapse(false);
                }
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.relativelayout:
                        Log.e("点击", "第" + position + "项");
                        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
                        if (holder != null) {
                            //设置relativelayout该条目的选中状态为false
                            //关闭已展开的该项
                            holder.relativelayout.setSelected(false);
                            holder.expandableLayout.collapse();

                            //展开的item项中的image关闭动作旋转
                            Animation close_anim = AnimationUtils.loadAnimation(context, R.anim.close_anim);
                            close_anim.setFillAfter(true);
                            holder.image.startAnimation(close_anim);
                        } else {
                            Log.e("初始化状态", "都没有展开，这是holder == null 执行当前语句");
                        }

                        if (position == selectedItem) {
                            selectedItem = UNSELECTED;
                            Log.e("关闭", "关闭");
                            // 初始化需要加载的动画资源
                            Animation close_anim = AnimationUtils.loadAnimation(context, R.anim.close_anim);
                            close_anim.setFillAfter(true);
                            // 将TextView执行Animation动画
                            image.startAnimation(close_anim);
                        } else {
                            adapter_two.setData(all_data.get(position).getList(), position);//设置数据

                            relativelayout.setSelected(true);
                            expandableLayout.expand();
                            selectedItem = position;
                            Log.e("展开", "展开");

                            // 初始化需要加载的动画资源
                            Animation open_anim = AnimationUtils.loadAnimation(context, R.anim.open_anim);
                            open_anim.setFillAfter(true);
                            // 将TextView执行Animation动画
                            image.startAnimation(open_anim);
                        }

                        //添加判断，如果用户触发的某一项和默认设置项一致，设置为原始状态，否则改变状态(用户一旦触发点击某一项事件，就算状态改变)
                        isfirst = selectedItem == defaultSelection;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static class SimpleTwoAdapter extends RecyclerView.Adapter<SimpleTwoAdapter.ViewHolder> {

        private List<String> list_datas;
        private int index;

        public SimpleTwoAdapter() {
        }

        public void setData(List<String> list_datas, int index) {
            this.list_datas = list_datas;
            this.index = index;
            notifyDataSetChanged();
        }

        public SimpleTwoAdapter(List<String> list_datas) {
            this.list_datas = list_datas;
        }

        @Override
        public SimpleTwoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_item, parent, false);
            return new SimpleTwoAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SimpleTwoAdapter.ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return list_datas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView item_tv;
            int position;

            public ViewHolder(View itemView) {
                super(itemView);
                item_tv = (TextView) itemView.findViewById(R.id.item_tv);
                item_tv.setOnClickListener(this);
            }

            public void bind(int position) {
                this.position = position;
                item_tv.setText(list_datas.get(position));
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.item_tv:
                        Log.e("列表" + index, "子项" + position);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    public List<DataBean> creatDatas() {
        List<DataBean> all_datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DataBean bean = new DataBean();
            bean.setGroup(i + "组");

            List<String> array = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                array.add(i + "组" + j + "项");
            }
            bean.setList(array);
            all_datas.add(bean);
        }
        return all_datas;
    }
}
