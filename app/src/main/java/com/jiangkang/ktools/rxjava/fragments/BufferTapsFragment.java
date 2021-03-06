package com.jiangkang.ktools.rxjava.fragments;


import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jiangkang.ktools.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * A simple {@link Fragment} subclass.
 */
public class BufferTapsFragment extends BaseRxJavaFragment {

    @BindView(R.id.btn_start_buffer_taps)
    Button mBtnStartBufferTaps;
    @BindView(R.id.btn_clear_log)
    Button mBtnClearLog;
    @BindView(R.id.tv_taps_counter)
    TextView mTvTapsCounter;
    @BindView(R.id.rc_buffer_taps)
    RecyclerView mRcBufferTaps;

    Unbinder unbinder;

    int counter;


    public BufferTapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buffer_taps, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getActivity().setTitle("点击累加");
        mRcBufferTaps.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRcBufferTaps.setAdapter(mAdapter);
        countTaps();
    }

    private void countTaps() {
        Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(final ObservableEmitter<Boolean> observableEmitter) throws Exception {
                        mBtnStartBufferTaps.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                log("点击了按钮");
                                mTvTapsCounter.setText(String.valueOf((++counter)));
                                observableEmitter.onNext(true);
                            }
                        });
                    }
                })
                .buffer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Boolean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(List<Boolean> list) {
                        if (list.size() != 0){
                            log("点击了" + list.size() + "次");
                            mTvTapsCounter.setText("0");
                            counter = 0;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log("subscriber -> onError:" + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        log("subscriber -> onCompleted");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick(R.id.btn_clear_log)
    public void onBtnClearLogClicked() {
        clearLogs();
    }


}
