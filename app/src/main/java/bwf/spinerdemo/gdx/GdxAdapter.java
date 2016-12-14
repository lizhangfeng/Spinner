package bwf.spinerdemo.gdx;

import android.content.Context;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

import bwf.spinerdemo.utils.DisplayUtil;

/**
 * Created by lizhangfeng on 16/6/20.
 * description: 骨骼动画ApplicationAdapter
 */
public class GdxAdapter extends ApplicationAdapter {


    OrthographicCamera camera;
    SpriteBatch batch;
    SkeletonRenderer renderer;

    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;

    SkeletonJson json;

    private Context context;

    public GdxAdapter(Context context) {
        this.context = context;
    }

    public void create() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.

        atlas = new TextureAtlas(Gdx.files.internal("spineboy/spineboy.atlas"));
//        atlas = new TextureAtlas(Gdx.files.internal("vae/xusong.atlas"));
        json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
//        json.setScale(0.5f); // Load the skeleton at 60% the size it was in Spine.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("spineboy/spineboy.json"));
//        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("vae/xusong.json"));

        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        skeleton.setPosition(DisplayUtil.getDensity_Width(context) / 2, DisplayUtil.getDensity_Height(context) / 3);

        AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.
//        stateData.setMix("run", "jump", 0.2f);
        stateData.setMix("jump", "run", 0.2f);

        state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
//        state.setTimeScale(0.5f); // Slow all animations down to 50% speed.

        // Queue animations on track 0.
        state.setAnimation(0, "run", true);

        state.addAnimation(0, "run", true, 0); // Run after the jump.
    }

    public void render() {
        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClearColor(0, 0, 0, 0);

        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        // Configure the camera, SpriteBatch, and SkeletonRendererDebug.
        camera.update();
        batch.getProjectionMatrix().set(camera.combined);

        batch.begin();
        renderer.draw(batch, skeleton); // Draw the skeleton images.
        batch.end();

    }

    public void resize(int width, int height) {
        camera.setToOrtho(false); // Update camera with new size.
    }

    public void dispose() {
        atlas.dispose();
    }


}
