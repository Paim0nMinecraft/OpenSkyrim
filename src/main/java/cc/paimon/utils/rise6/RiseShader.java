package cc.paimon.utils.rise6;



import java.util.List;


public abstract class RiseShader{
    public boolean active;

    public abstract void run(ShaderRenderType type, float partialTicks, List<Runnable> runnable);

    public abstract void update();
}
