package data;

import java.awt.Desktop;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class ImageOperator {
	
	public static final String deleted = "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRdEp0TNPk3cGnvN0IBtMZsw9-td381BgxHGoqEuiy8Afgn9qtc";
	
	public static void asyncLoadImage(ImageView imageView, String... urls) {
		String hash = String.join("", urls);

		if (imageDatabase.containsKey(hash)) {
			imageView.setImage(imageDatabase.get(hash));
			imageView.setFitHeight(imageView.getImage().getHeight());
			imageView.setFitWidth(imageView.getImage().getWidth());
			clipImageView(imageView);
			return;
		}
		
		imageView.setImage(wait);
		Task<Image> loadTask = new Task<Image>() {
			protected Image call() throws Exception {
				return getImageFrom(urls);
			}
			
			@Override
			protected void succeeded() {
				if(!getValue().isError()) {
					imageView.setImage(getValue());
					imageView.setFitHeight(imageView.getImage().getHeight());
					imageView.setFitWidth(imageView.getImage().getWidth());
					clipImageView(imageView);
				}
			}
		};
		Thread t = new Thread(loadTask);
		t.start();
	}
	
	public static void asyncLoadLargeIcon(ImageView imageView, String... urls) {
		String hash = String.join("", urls);

		if (imageDatabase.containsKey(hash)) {
			imageView.setImage(imageDatabase.get(hash));
			imageView.setFitHeight(50);
			imageView.setFitWidth(50);
			clipImageView(imageView);
			return;
		}
		
		imageView.setImage(wait);
		Task<Image> loadTask = new Task<Image>() {
			protected Image call() throws Exception {
				return getImageFrom(urls);
			}
			
			@Override
			protected void succeeded() {
				if(!getValue().isError()) {
					imageView.setImage(getValue());
					imageView.setFitHeight(50);
					imageView.setFitWidth(50);
					clipImageView(imageView);
				}
			}
		};
		Thread t = new Thread(loadTask);
		t.start();
	}
	
	public static void asyncLoadSmallIcon(ImageView imageView, String url) {

		if(lastSenderPhotosDatabase.containsKey(url)) {
			imageView.setImage(lastSenderPhotosDatabase.get(url));
			imageView.setFitHeight(0.66*50);
			imageView.setFitWidth(0.66*50);
			clipImageView(imageView);
			return;
		}
		imageView.setImage(wait_33);
		Task<Image> loadTask = new Task<Image>() {
			protected Image call() throws Exception {
				return getLastSenderPhotoFrom(url);
			}
			protected void succeeded() {
				if(!getValue().isError()) {
					imageView.setImage(getValue());
					imageView.setFitHeight(0.66*50);
					imageView.setFitWidth(0.66*50);
					clipImageView(imageView);
				}
			}
		};
		Thread t = new Thread(loadTask);
		t.start();
	}
	
	
	public static synchronized Image getLastSenderPhotoFrom(String url) {
		Image image = new Image(url, 50 * 0.66, 50 * 0.66, true, true);
		lastSenderPhotosDatabase.put(url, image);
		return image;
	}

	public static void clipImageView(ImageView image) {
		Rectangle rR = new Rectangle(0, 0, image.getFitWidth(), image.getFitHeight());
		rR.setArcHeight(10);
		rR.setArcWidth(10);
		image.setClip(rR);	
	}

	public static synchronized Image getImageFrom(String... urls) throws IIOException {
		
		String hash = String.join("", urls);

		if (urls.length == 1) {
			imageDatabase.put(hash, new Image(urls[0]));
			return imageDatabase.get(hash);
		}

		ArrayList<BufferedImage> BIs = new ArrayList<>();
		for (String url : urls) {
			addImage(BIs, url);
		}

		BufferedImage icon = new BufferedImage(210, 210, BufferedImage.TYPE_INT_ARGB);
		Iterator<BufferedImage> iter = BIs.iterator();
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
				if (iter.hasNext())
					icon.getGraphics().drawImage(iter.next(), 110 * i, 110 * j, null);
		AffineTransform at = new AffineTransform();
		at.scale(50 / 210., 50 / 210.);
		AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		icon = ato.filter(icon, new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB));
		imageDatabase.put(hash, SwingFXUtils.toFXImage(icon, null));

		return imageDatabase.get(hash);
	}

	private static void addImage(ArrayList<BufferedImage> BIs, String url) {
		try {
			BIs.add(ImageIO.read(new URL(url).openStream()));
		} catch (IOException e) {
			log.warn(Thread.currentThread().getName()+" is retrying to load image: "+url);
			addImage(BIs, url);
		}
	}
	
	public static void saveImageFromUrl(String url, File fullImageFile, Boolean open) {
		Thread thread = new Thread(() -> {
			try {
				BufferedImage fullImage;

				fullImage = ImageIO.read(new URL(url));
				Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
				ImageWriter writer = (ImageWriter) iter.next();
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwp.setCompressionQuality((float) 0.9);
				FileImageOutputStream output = new FileImageOutputStream(fullImageFile);
				writer.setOutput(output);
				IIOImage image = new IIOImage(fullImage, null, null);
				writer.write(null, image, iwp);
				if (open)
					Desktop.getDesktop().open(fullImageFile);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		thread.start();
	}

	private static HashMap<String, Image> imageDatabase = new HashMap<>();
	private static HashMap<String, Image> lastSenderPhotosDatabase = new HashMap<>();
	private static Image wait = new Image(ImageOperator.class.getResource("/assets/wait.png").toString());
	private static Image wait_33 = new Image(ImageOperator.class.getResource("/assets/wait.png").toString(), 33, 33, true, true);
	private static Logger log = LoggerFactory.getLogger(ImageOperator.class);
	
}
