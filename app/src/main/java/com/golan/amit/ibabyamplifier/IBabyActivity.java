package com.golan.amit.ibabyamplifier;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class IBabyActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    BabyPlayHelper bph;
    TextView tvCharDisplay;
    Button[] btnUser;
    RadioGroup radioGroup;
    Animation animRotateRight, animFadeOut;
    SoundPool sp;
    int[] winSound;
    int[] looseSound;
    int punchSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibaby);

        init();

        setListeners();

        play();

    }

    private void init() {
        bph = new BabyPlayHelper();
        tvCharDisplay = findViewById(R.id.tvDisplay);
        btnUser = new Button[]{
                findViewById(R.id.btnUser0), findViewById(R.id.btnUser1), findViewById(R.id.btnUser2),
                findViewById(R.id.btnUser3), findViewById(R.id.btnUser4), findViewById(R.id.btnUser5),
                findViewById(R.id.btnUser6), findViewById(R.id.btnUser7), findViewById(R.id.btnUser8),
                findViewById(R.id.btnUser9)
        };

        radioGroup = findViewById(R.id.radioBtnMode);
        /**
         * Animation
         */
        animRotateRight = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_right);
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        /**
         * Sound
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME). build();
            sp = new SoundPool.Builder()
                    .setMaxStreams(10).setAudioAttributes(aa).build();
        } else {
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }
        winSound = new int[] {
                sp.load(this, R.raw.applause, 1), sp.load(this, R.raw.cheering,1 ),
                sp.load(this, R.raw.lg0, 1), sp.load(this, R.raw.lg1, 1),
                sp.load(this, R.raw.lg2, 1), sp.load(this, R.raw.lg3, 1)
        };
        looseSound = new int[] {
                sp.load(this, R.raw.explode, 1), sp.load(this, R.raw.failtrombone, 1)
        };
        punchSound = sp.load(this, R.raw.punch, 1);
    }

    private void play() {
        bph.generate();
        if(MainActivity.DEBUG) {
            Log.d(MainActivity.DEBUGTAG, "generated random number: " + bph.getRandom_picker() +
                    ", char: " + bph.getCharAsString());
        }
        tvCharDisplay.setText(bph.getCharAsString());
        for (int i = 0; i < btnUser.length; i++) {
            btnUser[i].setText(bph.getCharByIndex(i));
        }
    }

    private void setListeners() {
        for (int i = 0; i < btnUser.length; i++) {
            btnUser[i].setOnClickListener(this);
        }

        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int tmpValue = -1;
        for (int i = 0; i < btnUser.length; i++) {
            if (v == btnUser[i]) {
                tmpValue = i;
                break;
            }
        }
        if (tmpValue == -1) {
            if(MainActivity.DEBUG) {
                Log.d(MainActivity.DEBUGTAG, "failed to find the right button clicked");
            }
            return;
        }
        if (tmpValue == bph.getRandom_picker()) {
            if(MainActivity.DEBUG) {
                Log.d(MainActivity.DEBUGTAG, "clicked the correct button!");
            }
            bph.generate();
            tvCharDisplay.setText(bph.getCharAsString());
            try {
                animRotateRight = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_right);
                animRotateRight.reset();
                animateAsync(animRotateRight);
                //  good random noise
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "animation right rotate exception");
            }
            bph.resetFails();
            sp.play(winSound[(int)(Math.random() * winSound.length)], 1, 1, 0, 0, 1);
            v.setAlpha((float)1);
        } else {
            if(MainActivity.DEBUG) {
                Log.d(MainActivity.DEBUGTAG, "clicked wrong character");
            }
            bph.increaseFails();
            if (bph.getFails() == BabyPlayHelper.FAILSNUMBER) {
                bph.resetFails();
                if(MainActivity.DEBUG) {
                    Log.d(MainActivity.DEBUGTAG, "clicked wrong character third time, switching image");
                }
                bph.generate();
                tvCharDisplay.setText(bph.getCharAsString());
                try {
                    animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                    animFadeOut.reset();
                    animateAsync(animFadeOut);
                    //  bad random noise
                } catch (Exception e) {
                    Log.e(MainActivity.DEBUGTAG, "animation fade out exception");
                }
                sp.play(looseSound[(int)(Math.random() * looseSound.length)], 1, 1, 0, 0, 1);
                v.setAlpha((float)1);
            } else {
                sp.play(punchSound, 1, 1, 0, 0, 1);
                v.setAlpha((float)0.8);
            }
        }
    }

    private void animateAsync(final Animation animation) {
        tvCharDisplay.clearAnimation();
        new AsyncTask<Animation, Void, Void>() {
            @Override
            protected Void doInBackground(Animation... animations) {
                animate(animation);
                return null;
            }
        }.execute();
    }


    private void animate(Animation animation) {
        tvCharDisplay.setAnimation(animation);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.digits:
                bph.setType_picker(BabyPlayHelper.DIGITS);
                break;
            case R.id.hebrew:
                bph.setType_picker(BabyPlayHelper.HEBREW);
                break;
            case R.id.english:
                bph.setType_picker(BabyPlayHelper.ENGLISH);
                break;
            default:
                Log.d(MainActivity.DEBUGTAG, "should never reach this switch block");
                break;
        }
        ;
        play();
    }
}
