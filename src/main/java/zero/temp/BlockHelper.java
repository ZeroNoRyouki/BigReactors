package zero.temp;

@Deprecated // this is a temporary fix
public final class BlockHelper {

    public static final byte[] SIDE_LEFT = { 4, 5, 5, 4, 2, 3 };
    public static final byte[] SIDE_RIGHT = { 5, 4, 4, 5, 3, 2 };
    public static final byte[] SIDE_OPPOSITE = { 1, 0, 3, 2, 5, 4 };
    public static final byte[] SIDE_ABOVE = { 3, 2, 1, 1, 1, 1 };
    public static final byte[] SIDE_BELOW = { 2, 3, 0, 0, 0, 0 };


    // Map which gives relative Icon to use on a block which can be placed on
    // any side.
    public static final byte[][] ICON_ROTATION_MAP = new byte[6][];

    static {
        ICON_ROTATION_MAP[0] = new byte[] { 0, 1, 2, 3, 4, 5 };
        ICON_ROTATION_MAP[1] = new byte[] { 1, 0, 2, 3, 4, 5 };
        ICON_ROTATION_MAP[2] = new byte[] { 3, 2, 0, 1, 4, 5 };
        ICON_ROTATION_MAP[3] = new byte[] { 3, 2, 1, 0, 5, 4 };
        ICON_ROTATION_MAP[4] = new byte[] { 3, 2, 5, 4, 0, 1 };
        ICON_ROTATION_MAP[5] = new byte[] { 3, 2, 4, 5, 1, 0 };
    }

}
