package com.noprom.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.noprom.view.GamePingtuLayout;


public class MainActivity extends Activity {

    // 游戏主界面
    private GamePingtuLayout mGamePingtuLayout;

    // 当前关卡数
    private TextView mLevel;

    // 当前所剩余的时间
    private TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLevel = (TextView) findViewById(R.id.id_level);
        mTime = (TextView) findViewById(R.id.id_time);
        mGamePingtuLayout = (GamePingtuLayout) findViewById(R.id.id_game_pintu);
        mGamePingtuLayout.setTimeEnabled(true);
        mGamePingtuLayout.setOnGamePintuListener(new GamePingtuLayout.GamePintuListener() {


            @Override
            public void timeChanged(int currentTime) {
                mTime.setText("" + currentTime);
            }

            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(MainActivity.this).setTitle("游戏信息").setMessage("升级~").setPositiveButton("下一关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGamePingtuLayout.nextLevel();
                        mLevel.setText("" + nextLevel);
                    }
                }).show();
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(MainActivity.this).setTitle("游戏信息").setMessage("游戏结束啦~").setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGamePingtuLayout.restartGame();
                    }
                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mGamePingtuLayout.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamePingtuLayout.resumeGame();
    }
}
