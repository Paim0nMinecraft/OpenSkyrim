package cc.paimon.utils;

import net.minecraft.util.math.BlockPos;

public class nBlockPos extends BlockPos {
    private int x;
    private int y;
    private int z;

    public nBlockPos() {
        super(0, 0, 0);
    }

    public void set(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    public void setZ(final int z) {
        this.z = z;
    }
}
