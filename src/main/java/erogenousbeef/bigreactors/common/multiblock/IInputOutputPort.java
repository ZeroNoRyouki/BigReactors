package erogenousbeef.bigreactors.common.multiblock;

public interface IInputOutputPort {

    enum Direction {
        Input,
        Output;

        public Direction opposite() {
            return Input == this ? Output : Input;
        }

        public boolean isInput() {
            return Input == this;
        }

        public static Direction from(boolean isInput) {
            return isInput ? Input : Output;
        }
    }

    Direction getDirection();

    void setDirection(Direction direction, boolean markForUpdate);

    void toggleDirection(boolean markForUpdate);

}
