package net.ccbluex.liquidbounce.utils.render.blur;

import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.shader.Framebuffer;

import java.util.ArrayList;
import java.util.List;

public class KawaseBlur extends MinecraftInstance {

    private static final List<Framebuffer> framebufferList = new ArrayList<>();
    public static ShaderUtil kawaseDown = new ShaderUtil("liquidwing/shader/fragment/kawasedown.frag");
    public static ShaderUtil kawaseUp = new ShaderUtil("liquidwing/shader/fragment/kawaseup.frag");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
    private static int currentIterations;

    public static void setupUniforms(float offset) {
        kawaseDown.setUniformf("offset", offset, offset);
        kawaseUp.setUniformf("offset", offset, offset);
    }

    private static void initFramebuffers(float iterations) {
        for (Framebuffer framebuffer : framebufferList) {
            framebuffer.deleteFramebuffer();
        }
        framebufferList.clear();

        framebufferList.add(RenderUtils.createFrameBuffer(framebuffer));


        for (int i = 1; i <= iterations; i++) {
            Framebuffer framebuffer = new Framebuffer(minecraft.displayWidth, minecraft.displayHeight, false);
            //  framebuffer.setFramebufferFilter(GL11.GL_LINEAR);
            framebufferList.add(RenderUtils.createFrameBuffer(framebuffer));
        }
    }


    public static void renderBlur(int iterations, int offset) {
        if (currentIterations != iterations) {
            initFramebuffers(iterations);
            currentIterations = iterations;
        }

        renderFBO(framebufferList.get(1), minecraft.getFramebuffer().framebufferTexture, kawaseDown, offset);

        //Downsample
        for (int i = 1; i < iterations; i++) {
            renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, offset);
        }

        //Upsample
        for (int i = iterations; i > 1; i--) {
            renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, offset);
        }


        minecraft.getFramebuffer().bindFramebuffer(true);

        RenderUtils.bindTexture(framebufferList.get(1).framebufferTexture);
        kawaseUp.init();
        kawaseUp.setUniformf("offset", offset, offset);
        kawaseUp.setUniformf("halfpixel", 0.5f / minecraft.displayWidth, 0.5f / minecraft.displayHeight);
        kawaseUp.setUniformi("inTexture", 0);
        ShaderUtil.drawQuads();
        kawaseUp.unload();

    }

    private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtil shader, float offset) {
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        shader.init();
        RenderUtils.bindTexture(framebufferTexture);
        shader.setUniformf("offset", offset, offset);
        shader.setUniformi("inTexture", 0);
        shader.setUniformf("halfpixel", 0.5f / minecraft.displayWidth, 0.5f / minecraft.displayHeight);
        ShaderUtil.drawQuads();
        shader.unload();
        framebuffer.unbindFramebuffer();
    }
}
