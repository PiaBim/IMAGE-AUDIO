package com.example.a0808;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

class Sprite {
    protected int x, y; // 현재좌표
    protected int width, height; // 화면의 크기
    protected int dx, dy; // 속도
    private Bitmap bitmap; // 이미지
    protected int id; // 이미지 리소스 아이디
    private RectF rectF; // 충돌 검사

    public Sprite(Context context, int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        rectF = new RectF();
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    // 스프라이트를 화면에 그림
    public void draw(Canvas g, Paint p) {
        g.drawBitmap(bitmap, x, y, p);
    }

    // 스프라이트 움직임
    public void move() {
        x += dx;
        y += dy;
        rectF.left = x;
        rectF.top = y;
        rectF.right = x + width;
        rectF.bottom = y + height;
    }

    public void setDx(int dx) {this.dx = dx;}
    public void setDy(int dy) {this.dy = dy;}
    public int getDx() {return dx;}
    public int getDy() {return dy;}
    public int getX() {return x;}
    public int getY() {return y;}
    public RectF getRectF() {return rectF;}

    // 다른 스프라이트와의 충돌 여부 충돌이면 true
    public boolean checkCollision(Sprite other) {
        return rectF.intersect(other.getRectF());
    }

    // 충돌을 처리
    public void handleCollision(Sprite other) {
    }
}

// 플레이어 우주선
class StarShipSprite extends Sprite {
    SpaceInvadersView game;

    public StarShipSprite(Context context, SpaceInvadersView game, int x, int y) {
        super(context, R.drawable.starship, x, y);
        this.game = game;
        dx = 0;
        dy = 0;
    }

    @Override
    // 우주선의 움직임
    public void move() {
        if ((dx < 0) && (x < 10)) {
            return;
        }
        if ((dx > 0) && (x > 800)) {
            return;
        }
        super.move();
    }

    @Override
    // 충돌 할 경우
    public void handleCollision(Sprite other) {
        if (other instanceof AlienSprite) {
            game.setGameOver(true);
        }
    }
}

// 적 우주선
class AlienSprite extends Sprite {
    private SpaceInvadersView game;

    public AlienSprite(Context context, SpaceInvadersView game, int x, int y) {
        super(context, R.drawable.alien, x, y);
        this.game = game;
        dx = -3;
    }

    @Override
    public void move() {
        if (((dx < 0) && (x < 10)) || ((dx > 0) && (x > 800))) {
            dx = -dx;
            y += 80;
            if (y > 600) {
                game.endGame();
            }
        }
        super.move();
    }
}

// 우주선에서 발사하는 미사일
class ShotSprite extends Sprite {
    private SpaceInvadersView game;

    public ShotSprite(Context context, SpaceInvadersView game, int x, int y) {
        super(context, R.drawable.fire, x, y);
        this.game = game;
        dy = -16;
    }

    @Override
    public void move() {
        super.move();
        if (y < -100) {
            game.removeSprite(this);
        }
    }

    @Override
    public void handleCollision(Sprite other) {
        if (other instanceof AlienSprite) {
            game.removeSprite(this);
            game.removeSprite(other);
        }
    }
}

// 게임을 진행하는 메인 클래스
class SpaceInvadersView extends SurfaceView implements Runnable {
    private Context context;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;

    private volatile boolean running;
    private Canvas canvas;
    private Paint paint;
    private int screenW, screenH;

    private CopyOnWriteArrayList<Sprite> sprites = new CopyOnWriteArrayList<>();
    private Sprite startship;
    private boolean gameOver = false;

    public SpaceInvadersView(Context context, int x, int y) {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenW = x;
        screenH = y;
        startGame();
    }

    public void initSprites() {
        startship = new StarShipSprite(context, this, screenW / 2, screenH - 400);
        sprites.add(startship);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 8; x++) {
                Sprite alien = new AlienSprite(context, this, 100 + (x * 100), (50) + y * 100);
                sprites.add(alien);
            }
        }
    }

    @Override
    // 게임 루프
    public void run() {
        while (running) {
            if (gameOver) {
                return;
            }
            //캐릭터를 이동
            for (int i = 0; i < sprites.size(); i++) {
                Sprite sprite = sprites.get(i);
                sprite.move();
            }

            for (int p = 0; p < sprites.size(); p++) {
                for (int s = p + 1; s < sprites.size(); s++) {
                    Sprite me = sprites.get(p);
                    Sprite other = sprites.get(s);
                    if (me.checkCollision(other)) {
                        me.handleCollision(other);
                        other.handleCollision(me);
                    }
                }
            }
            //캐릭터를 다시 그림
            draw();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.BLUE);
            paint.setColor(Color.BLUE);
            for (Sprite sprite : sprites) {
                sprite.draw(canvas, paint);
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }
    //게임 시작
    private void startGame() {
        sprites.clear();
        initSprites();
    }
    //게임 종료
    public void endGame() {
        System.exit(0);
    }
    //총알을발사
    public void fire() {
        ShotSprite shot = new ShotSprite(context, this, startship.getX() + 10, startship.getY() - 30);
        sprites.add(shot);
    }
    //스프라이트 제거용
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }
    //일시 정지
    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // 재시작
    public void resume() {
        running = true;
        gameThread = new Thread(this);  // 스레드가 생성되고 시작
        gameThread.start();
    }
    // 게임 오버
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    @Override
    //터치가 발생하면 우주선을 이동
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int switchInt = motionEvent.getAction() & MotionEvent.ACTION_MASK;
        switch (switchInt) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getY() > screenH * 4 / 5)
                    if (motionEvent.getX() > screenW / 2)
                        startship.setDx(+10);
                    else
                        startship.setDx(-10);
                if (motionEvent.getY() <= screenH * 4 / 5) {
                    fire();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startship.setDx(0);
                break;
        }
        return true;
    }
}

public class MainActivity extends AppCompatActivity {
    SpaceInvadersView spaceInvadersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display=getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        spaceInvadersView = new SpaceInvadersView(this, size.x, size.y);
        setContentView(spaceInvadersView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        spaceInvadersView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        spaceInvadersView.pause();
    }
}
