package data;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class ImageOperator {
	
	public static void clipImage(ImageView image) {
		Rectangle rR = new Rectangle(0, 0, image.getImage().getWidth(),
				image.getImage().getHeight());
		rR.setArcHeight(10);
		rR.setArcWidth(10);
		image.setClip(rR);
	}
	
	public static Image getIconFrom(String... urls) {
		if (urls.length == 1) 
			return new Image(urls[0]);
		
		ArrayList<BufferedImage> BIs = new ArrayList<>();
		for (String url : urls) {
			try {
				BIs.add(ImageIO.read(new URL(url)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		BufferedImage icon = new BufferedImage(110, 110, BufferedImage.TYPE_INT_ARGB);
		Iterator<BufferedImage> iter = BIs.iterator();
		for (int i=0; i<2; i++) 
			for (int j=0; j<2; j++)
				if(iter.hasNext())
					icon.getGraphics().drawImage(iter.next(), 60*i, 60*j, null);
		AffineTransform at = new AffineTransform();
		at.scale(50/110., 50/110.);
		AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		icon = ato.filter(icon, new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB));
		return SwingFXUtils.toFXImage(icon, null);
	}
}
