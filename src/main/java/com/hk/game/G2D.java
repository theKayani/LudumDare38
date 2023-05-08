package com.hk.game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class G2D
{
	private int flags;
	public final int width;
	public final int height;
	private final List<AffineTransform> matrices;
	private final List<Color> colors;
	private final List<Font> fonts;
	public Graphics2D g2d;
	private final Double rect = new Double();
	private final Ellipse2D.Double circle = new Ellipse2D.Double();
	private final Line2D.Double line = new Line2D.Double();
	private final RoundRectangle2D.Double roundRect = new RoundRectangle2D.Double();
	public static final int G_FILL = 1;
	public static final int G_CENTER = 2;

	public G2D(int width, int height)
	{
		this.width = width;
		this.height = height;
		matrices = new ArrayList<>();
		colors = new ArrayList<>();
		fonts = new ArrayList<>();
	}

	public G2D reset()
	{
		matrices.clear();
		colors.clear();
		fonts.clear();
		return this;
	}

	public G2D enable(int bit)
	{
		flags |= bit;
		return this;
	}

	public boolean isEnabled(int bit)
	{
		return (flags & bit) != 0;
	}

	public G2D disable(int bit)
	{
		flags &= ~bit;
		return this;
	}

	public G2D pushMatrix()
	{
		matrices.add(g2d.getTransform());
		return this;
	}

	public AffineTransform getMatrix()
	{
		return g2d.getTransform();
	}

	public G2D setMatrix(AffineTransform matrix)
	{
		g2d.setTransform(matrix);
		return this;
	}

	public G2D popMatrix()
	{
		g2d.setTransform(matrices.remove(matrices.size() - 1));
		return this;
	}

	public G2D pushColor()
	{
		colors.add(getColor());
		return this;
	}

	public Color getColor()
	{
		return g2d.getColor();
	}

	public G2D setColor(int r, int g, int b)
	{
		return setColor(new Color(r, g, b));
	}

	public G2D setColor(int r, int g, int b, int a)
	{
		return setColor(new Color(r, g, b, a));
	}

	public G2D setColor(float r, float g, float b)
	{
		return setColor(new Color(r, g, b));
	}

	public G2D setColor(float r, float g, float b, float a)
	{
		return setColor(new Color(r, g, b, a));
	}

	public G2D setColor(int rgb)
	{
		return setColor(rgb, false);
	}

	public G2D setColor(int clr, boolean alpha)
	{
		return setColor(new Color(clr, alpha));
	}

	public G2D setColor(Color color)
	{
		g2d.setColor(color);
		return this;
	}

	public G2D popColor()
	{
		setColor(colors.remove(colors.size() - 1));
		return this;
	}

	public G2D pushFont()
	{
		fonts.add(g2d.getFont());
		return this;
	}

	public Font getFont()
	{
		return g2d.getFont();
	}

	public G2D setFont(Font font)
	{
		g2d.setFont(font);
		return this;
	}

	public G2D setFontSize(float size)
	{
		setFont(getFont().deriveFont(size));
		return this;
	}

	public G2D popFont()
	{
		g2d.setFont(fonts.remove(fonts.size() - 1));
		return this;
	}

	public G2D clearRect(double x, double y, double width, double height)
	{
		g2d.clearRect((int)x, (int)y, (int)width, (int)height);
		return this;
	}

	public G2D clearRect(RectangularShape rect)
	{
		clearRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
		return this;
	}

	public G2D drawImage(ImageIcon image, double x, double y)
	{
		x = (int)getCenteredX(x, image.getIconWidth());
		y = (int)getCenteredY(y, image.getIconHeight());
		g2d.drawImage(image.getImage(), (int)x, (int)y, null);
		return this;
	}

	public G2D drawImage(Image image, double x, double y)
	{
		x = (int)getCenteredX(x, image.getWidth(null));
		y = (int)getCenteredY(y, image.getHeight(null));
		g2d.drawImage(image, (int)x, (int)y, null);
		return this;
	}

	public G2D drawImage(ImageIcon image)
	{
		return drawImage(image, 0.0D, 0.0D);
	}

	public G2D drawRectangle(double x, double y, double width, double height)
	{
		rect.x = getCenteredX(x, width);
		rect.y = getCenteredY(y, height);
		rect.width = width;
		rect.height = height;
		return drawOrFillShape(rect);
	}

	public G2D drawRoundRectangle(double x, double y, double width, double height, double arcWidth, double arcHeight)
	{
		roundRect.x = getCenteredX(x, width);
		roundRect.y = getCenteredY(y, height);
		roundRect.width = width;
		roundRect.height = height;
		roundRect.arcwidth = arcWidth;
		roundRect.archeight = arcHeight;
		return drawOrFillShape(roundRect);
	}

	public G2D drawPoint(double x, double y)
	{
		drawLine(x, y, x, y);
		return this;
	}

	public G2D drawCircle(double x, double y, double radius)
	{
		return drawEllipse(x, y, radius * 2.0D, radius * 2.0D);
	}

	public G2D drawEllipse(double x, double y, double width, double height)
	{
		circle.x = getCenteredX(x, width);
		circle.y = isEnabled(2) ? y - height / 2.0D : y;
		circle.width = width;
		circle.height = height;
		return drawOrFillShape(circle);
	}

	public G2D drawLine(double x1, double y1, double x2, double y2)
	{
		line.x1 = x1;
		line.x2 = x2;
		line.y1 = y1;
		line.y2 = y2;
		return drawOrFillShape(line);
	}

	public G2D drawString(Object s, double x, double y)
	{
		String str = String.valueOf(s);
		Rectangle2D r = g2d.getFontMetrics().getStringBounds(str, g2d);
		float x1 = (float)getCenteredX(x, r.getWidth());
		float y1 = (float)getCenteredY(y, r.getHeight());
		g2d.drawString(str, x1, y1);
		return this;
	}

	public G2D drawPolygon(int[] xPoints, int[] yPoints)
	{
		g2d.drawPolygon(xPoints, yPoints, Math.min(xPoints.length, yPoints.length));
		return this;
	}

	public G2D drawPolyline(int[] xPoints, int[] yPoints)
	{
		g2d.drawPolyline(xPoints, yPoints, Math.min(xPoints.length, yPoints.length));
		return this;
	}

	public G2D drawShape(Shape shape)
	{
		if (isEnabled(G_CENTER) && shape instanceof RectangularShape)
		{
			RectangularShape rect = (RectangularShape)shape;
			shape = new Double(rect.getX() - rect.getWidth() / 2.0D, rect.getY() - rect.getHeight() / 2.0D, rect.getWidth(), rect.getHeight());
		}

		return drawOrFillShape(shape);
	}

	public G2D translate(double x, double y)
	{
		g2d.translate(x, y);
		return this;
	}

	public G2D scale(double x, double y)
	{
		g2d.scale(x, y);
		return this;
	}

	public G2D shear(double x, double y)
	{
		g2d.shear(x, y);
		return this;
	}

	public G2D rotate(double degrees, double x, double y)
	{
		g2d.rotate(Math.toRadians(degrees), x, y);
		return this;
	}

	public G2D rotate(double degrees)
	{
		g2d.rotate(Math.toRadians(degrees));
		return this;
	}

	public G2D rotateR(double radians, double x, double y)
	{
		g2d.rotate(radians, x, y);
		return this;
	}

	public G2D rotateR(double radians)
	{
		g2d.rotate(radians);
		return this;
	}

	public Rectangle2D getStringBounds(String s)
	{
		return g2d.getFontMetrics().getStringBounds(s, g2d);
	}

	private G2D drawOrFillShape(Shape shape)
	{
		if (isEnabled(G_FILL))
			g2d.fill(shape);
		else
			g2d.draw(shape);

		return this;
	}

	private double getCenteredX(double x, double width)
	{
		return isEnabled(G_CENTER) ? x - width / 2.0D : x;
	}

	private double getCenteredY(double y, double height)
	{
		return isEnabled(G_CENTER) ? y + height / 2.0D : y;
	}

	public Graphics2D getGraphics()
	{
		return g2d;
	}
}

