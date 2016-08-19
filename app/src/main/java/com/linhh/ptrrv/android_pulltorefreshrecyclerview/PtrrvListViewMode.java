package com.linhh.ptrrv.android_pulltorefreshrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.lhh.ptrrv.library.footer.loadmore.BaseLoadMoreView;

/**
 * Created by Linhh on 15/11/15.
 */
public class PtrrvListViewMode  extends AppCompatActivity {

    private PullToRefreshRecyclerView mPtrrv;
    private PtrrvAdapter mAdapter;
    private static final int DEFAULT_ITEM_SIZE = 20;
    private static final int ITEM_SIZE_OFFSET = 20;

    private static final int MSG_CODE_REFRESH = 0;
    private static final int MSG_CODE_LOADMORE = 1;

    private static final int TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        findViews();

    }

    private void findViews(){
        mPtrrv = (PullToRefreshRecyclerView) this.findViewById(R.id.ptrrv);
        mPtrrv.setSwipeEnable(true);//open swipe
        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(this, mPtrrv.getRecyclerView());
        loadMoreView.setLoadmoreString(getString(R.string.demo_loadmore));
        loadMoreView.setLoadMorePadding(100);
        mPtrrv.setLayoutManager(new LinearLayoutManager(this));
        mPtrrv.setPagingableListener(new PullToRefreshRecyclerView.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                mHandler.sendEmptyMessageDelayed(MSG_CODE_LOADMORE, TIME);
            }
        });
        mPtrrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(MSG_CODE_REFRESH, TIME);
            }
        });
        mPtrrv.getRecyclerView().addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mPtrrv.addHeaderView(View.inflate(this, R.layout.header, null));
        mPtrrv.setEmptyView(View.inflate(this,R.layout.empty_view,null));
//        mPtrrv.removeHeader();
        mPtrrv.setLoadMoreFooter(loadMoreView);
        mPtrrv.getLoadMoreFooter().setOnDrawListener(new BaseLoadMoreView.OnDrawListener() {
            @Override
            public boolean onDrawLoadMore(Canvas c, RecyclerView parent) {
                Log.i("onDrawLoadMore","draw load more");
                return false;
            }
        });
        mAdapter = new PtrrvAdapter(this);
//        mAdapter.setCount(0);
        mAdapter.setCount(DEFAULT_ITEM_SIZE);
        mPtrrv.setAdapter(mAdapter);
        mPtrrv.onFinishLoading(true, false);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE_REFRESH) {
                mAdapter.setCount(DEFAULT_ITEM_SIZE);
                mAdapter.notifyDataSetChanged();
                mPtrrv.setOnRefreshComplete();
                mPtrrv.onFinishLoading(true, false);
            } else if (msg.what == MSG_CODE_LOADMORE) {
                if(mAdapter.getItemCount() == DEFAULT_ITEM_SIZE + ITEM_SIZE_OFFSET){
                    //over
                    Toast.makeText(PtrrvListViewMode.this, R.string.nomoredata, Toast.LENGTH_SHORT).show();
                    mPtrrv.onFinishLoading(false, false);
                }else {
                    mAdapter.setCount(DEFAULT_ITEM_SIZE + ITEM_SIZE_OFFSET);
                    mAdapter.notifyDataSetChanged();
                    mPtrrv.onFinishLoading(true, false);
                }
            }
        }
    };

    private class PtrrvAdapter extends PtrrvBaseAdapter<PtrrvAdapter.ViewHolder> {
        public PtrrvAdapter(Context context) {
            super(context);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.ptrrv_item, null);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
