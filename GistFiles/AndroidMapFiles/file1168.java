final class SubmitResult {
    final boolean inProgress;
    final boolean success;
    final String errorMessage;


    public SubmitResult(boolean inProgress, boolean success, String errorMessage) {
        this.inProgress = inProgress;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    static SubmitResult idle() {
        return new SubmitResult(false, false, null);
    }
    static SubmitResult inProgress() {
        return new SubmitResult(true, false, null);
    }
    static SubmitResult success() {
        return new SubmitResult(false, true, null);
    }
    static SubmitResult failure (String errorMessage) {
        return new SubmitResult(false, false, errorMessage);
    }
}
