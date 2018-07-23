package zsy.jt.com.cct2.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.animation.BaseAnimation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import zsy.jt.com.cct2.R;
import zsy.jt.com.cct2.adapter.ShowRecyclerAdapter;
import zsy.jt.com.cct2.base.BaseFragment;
import zsy.jt.com.cct2.bean.MqBean;
import zsy.jt.com.cct2.utils.ScaleItemAnimator;
import zsy.jt.com.cct2.utils.ThreadPoolManager;

public class ShowRecyclerFragment extends BaseFragment {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    Unbinder unbinder;
    private ShowRecyclerAdapter adapter;
    private List<MqBean> list;
    private boolean deleteing = true;

    public ShowRecyclerFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_show_recycler;
    }

    @Override
    protected void initView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        list = new ArrayList<>();
        adapter = new ShowRecyclerAdapter(list);
        recycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(adapter);
      adapter.openLoadAnimation(new BaseAnimation() {
            @Override
            public Animator[] getAnimators(View view) {
                return new Animator[]{
                        ObjectAnimator.ofFloat(view, "scaleY", 0, 1.1f, 1),
                        ObjectAnimator.ofFloat(view, "scaleX", 0, 1.1f, 1)
                };
            }
        });
        recycler.setItemAnimator(new ScaleItemAnimator());
        recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 5;
                outRect.right = 5;
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initHttp() {

    }

    private void deleteData() {

        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                while (deleteing) {
                    try {
                        Thread.sleep(1000);

                        for (int i = 0; i < list.size(); i++) {
                            long nowTime = System.currentTimeMillis();
                            long date = list.get(i).getDate();
                            Log.e("---", "now:" + nowTime + "---date:" + date);
                            if ((nowTime - date) > 4000) {
                                if (i >= list.size()) {
                                    return;
                                }
                                final int finalI = i;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(list.size()>0) {
                                            list.remove(finalI);
                                            adapter.notifyItemRemoved(finalI);
                                        }
                                    }
                                });

                                return;
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThread(MqBean mqBean) {
        if (mqBean != null && mqBean.toString().length() > 0) {
            mqBean.setDate(System.currentTimeMillis());
            list.add(mqBean);
            adapter.notifyItemChanged(list.size() - 1);
            recycler.smoothScrollToPosition(list.size() - 1);
            deleteData();

            if (list.size() > 3) {
                list.remove(0);
                adapter.notifyItemRemoved(0);
            }

        }
    }

}
