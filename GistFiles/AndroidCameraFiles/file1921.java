/**
 * GUI effects like flash lights and vibration have own rhythm
 * Rhythm consist of short and long signals like Morse dictionary
 * So this enum is a part of rhythm
 */
public enum Morse {
    Dot(400),  // replace hardcode with Constants
    Dash(800); // replace hardcode with Constants

    private final int duration;

    Morse(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }