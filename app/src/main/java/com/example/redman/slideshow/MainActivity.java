package com.example.redman.slideshow;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ImageSwitcher mImageSwitcher;
    int[] mImageResources = {R.drawable.slide00,R.drawable.slide01,R.drawable.slide02,R.drawable.slide03,R.drawable.slide04,R.drawable.slide05,R.drawable.slide06,R.drawable.slide07,R.drawable.slide08,R.drawable.slide09};
    int mPosition =0;
    MediaPlayer mMediaPlayer;

    //スライドショー実行時のタイマー処理
    boolean mIsSlideShow = false;
    Timer mTimer = new Timer();
    TimerTask mTimerTask = new MainTimerTask();
    TimerTask commentTimerTask = new CommentTimerTask();
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher);
        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });
        //最初に表示する画像を指定
        mImageSwitcher.setImageResource(mImageResources[0]);

        //スライドショーの時間設定(5秒でスライド切り替え)
        mTimer.schedule(mTimerTask,0,5000);

        //コメントの時間設定
        mTimer.schedule(commentTimerTask,0,3000);

        //スライドショー実行中の表示をデフォルトで非表示
        findViewById(R.id.slideshowActiveText).setAlpha(0);

        //コメントは非表示
        TextView nicoComment = (TextView) findViewById(R.id.nicoComment);
        Button commentButton = (Button) findViewById(R.id.commentButton);
        nicoComment.setVisibility(View.INVISIBLE);
        commentButton.setVisibility(View.INVISIBLE);

        //メディアプレイヤー設定
        mMediaPlayer = MediaPlayer.create(this, R.raw.delight);
        mMediaPlayer.setLooping(true);
        final CheckBox bgmCheckBox = (CheckBox)findViewById(R.id.bgm_checkbox);
        bgmCheckBox.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view){
                switchBgmCheckbox(bgmCheckBox.isChecked());
            }
        });
    }

    //コメントフォームを表示する
    public void showNicoComment(View view) {
        TextView nicoComment = (TextView) findViewById(R.id.nicoComment);
        commentBack(view);
        Button commentButton = (Button) findViewById(R.id.commentButton);
        nicoComment.setVisibility(View.VISIBLE);
        commentButton.setVisibility(View.VISIBLE);
    }

    //コメントを流した後に元の位置に戻す
    public void commentBack(View view){
        TextView nicoComment = (TextView) findViewById(R.id.nicoComment);
        if(nicoComment.getVisibility() == View.VISIBLE){
            nicoComment.animate().setDuration(0).setInterpolator(new LinearInterpolator()).x(view.getX());
        }
    }

    //スライド移動設定
    private void movePosition(int move){
        mPosition = mPosition + move;
        if(mPosition > mImageResources.length -1){
            mPosition = 0;
        }else if(mPosition < 0){
            mPosition = mImageResources.length -1;
        }
        mImageSwitcher.setImageResource(mImageResources[mPosition]);
    }

    //前へボタン押下時
    public void onPrevButtonTapped(View view){
        //表示画像を一つ前に戻す
        movePosition(-1);
        //切り替え時のアニメーション
        mImageSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mImageSwitcher.setOutAnimation(this, android.R.anim.fade_out);
    }

    //次へボタン押下時
    public void onNextButtonTapped(View view){
        //表示画像を一つ後に進める
        movePosition(1);
        //切り替え時のアニメーション
        mImageSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mImageSwitcher.setOutAnimation(this, android.R.anim.fade_out);
    }

    //スライドショー時の自動切換え用並列処理
    public class MainTimerTask extends TimerTask{
        @Override
        public void run(){
            if(mIsSlideShow){
                mHandler.post(new Runnable(){
                    @Override
                    public void run(){
                        movePosition(1);
                    }
                });
            }
        }
    }

    //コメント用並列処理
    public class CommentTimerTask extends TimerTask{
        @Override
        public void run(){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    final Button commentButton = (Button) findViewById(R.id.commentButton);
                    commentButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            TextView nicoComment = (TextView) findViewById(R.id.nicoComment);
                            float x = nicoComment.getX()- 800;
                            nicoComment.animate().setDuration(3000).setInterpolator(new LinearInterpolator()).x(x);
                            commentButton.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });
        }
    }


    //スライドショー実行ボタン押下時
    public void onSlideshowButtonTapped(View view){
        mIsSlideShow = !mIsSlideShow;
        if(mIsSlideShow){
            findViewById(R.id.slideshowActiveText).setAlpha(1);
        }else{
            findViewById(R.id.slideshowActiveText).setAlpha(0);
        }
    }

    //BGM再生するかどうかを切り替え
    private void switchBgmCheckbox(boolean check_status){
        if(check_status){
            mMediaPlayer.start();
        }else{
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
        }
    }

}
