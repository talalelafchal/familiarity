@EView
public class MagneticPlateView extends View {
    // ...중략...
    
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.save();
        drawIronPipe(canvas);
        drawMagneticBall(canvas);
        canvas.restore();

    }

    private void drawMagneticBall(Canvas canvas) {
        canvas.drawCircle(ballCenterPoint.x, ballCenterPoint.y, ballRadious, ballPaint);
    }


    private void drawIronPipe(Canvas canvas) {

        final int ballCenterX = ballCenterPoint.x;
        final int ballCenterY = ballCenterPoint.y;

        for (int rowIdx = 0; rowIdx < pipeRowCount; ++rowIdx) {
            for (int colIdx = 0; colIdx < pipeColumnCount; ++colIdx) {

                int startX = colIdx * (pipeLength + pipeMargin);
                int startY = rowIdx * pipeLength;

                double radian = Math.atan2(ballCenterX - startX, ballCenterY - startY);

                int stopX = (int) (startX + Math.sin(radian) * pipeLength);
                int stopY = (int) (startY + Math.cos(radian) * pipeLength);

                canvas.drawLine(startX, startY, stopX, stopY, pipePaint);
            }
        }

    }
}