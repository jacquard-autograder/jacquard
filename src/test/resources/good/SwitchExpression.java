public class SwitchExpression {
    public void fun() {
        return switch (behavior) {
            case Passive -> false;
            case Boss, Hostile -> true;
            case Neutral -> getStatus() == Status.Injured;
        };
    }
}
