
import java.awt.*;
import java.applet.Applet;
import java.net.URL;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public final class ImageResize extends Applet implements Runnable, KeyListener
{

	static final String loadingMsg = "Please wait while calculating data...";

	
	// The thread the drive the animation
	Thread runner=null;

	// timing info
	long ThenMS,NowMS;

	// the double buffer
	Image doubleBufferImage;
	Graphics doubleBufferGraphics;

	// the pixel array and MIS stuff
	int TargetPixels[];
	ColorModel TargetCM;
	MemoryImageSource TargetMIS;
	Image TargetImage;

	public int width;
	public int height;
	
	// Saves creating one every time
	private int keyCode;
	
	// This refers to the old mouse position
	int oldx,oldy;
	final float deg2rad=(float)3.14159265/180;
	boolean initialized;
	
	// This is applet specific
	int maxImageSizeX,maxImageSizeY, minImageSizeX, minImageSizeY;
	String imageName;
	int sizeX;
	int sizeY;
	int newSizeX;
	int newSizeY;
	int imagePixelArray[];
	int BlankPixels[];
	int black = 0xFF000000;
	int changeX;
	int changeY;
	
	public void init()
	{
	}
	
	public void initialize()
	{
		initialized = false;
		
		// get size of applet
		width=getSize().width;
		height=getSize().height;
		
		//Get Parameters from HTML file
		getParameters();

		
		//Initialize Texture
		initImages();
		
		newSizeX = sizeX;
		newSizeY = sizeY;
		
		// create a double buffer
		doubleBufferImage=createImage(width,height);
		doubleBufferGraphics=doubleBufferImage.getGraphics();

		// This array contains the 32 bit pixels of the screen.
		TargetPixels=new int[width*height];
		TargetCM = ColorModel.getRGBdefault();
		TargetMIS=new MemoryImageSource(width,height,TargetCM,TargetPixels,0,width);
		TargetMIS.setAnimated(true);
		TargetMIS.setFullBufferUpdates(true);
		TargetImage=createImage(TargetMIS);

		// Respond to key presses
		addKeyListener( this );

		
		BlankPixels = new int[ width * height ];
		for ( int index = 0; index < BlankPixels.length; index++ )
		{
				BlankPixels[ index ] = black;
		}
		
		
		// do garbage collection
		System.gc();
		
		initialized = true;
	}
	

	public void getParameters()
	{
		// Getting parameters for the applet from the HTML
		imageName = getParameter("image");
		maxImageSizeX = Integer.parseInt( getParameter("maxImageSizeX") );
		maxImageSizeY = Integer.parseInt( getParameter("maxImageSizeY") );
		minImageSizeX = Integer.parseInt( getParameter("minImageSizeX") );
		minImageSizeY = Integer.parseInt( getParameter("minImageSizeY") );
	}
	
	public void start()
	{
		//
		// start the demo running
		//

		// check if the demo is running already
		if(runner==null){
			// initialize the timer
		        ThenMS=System.currentTimeMillis();

		        // create a new thread and grab max priority
		        runner=new Thread(this);
				runner.setPriority(Thread.MAX_PRIORITY);
				
				// set the thread going
		        runner.start();
		}
	}


	public void stop()
	{
		//
		// stop the demo running
		//

		// check if the demo is running
		if(runner!=null){
			// stop the demo running
			runner.stop();
			runner=null;
		}

		try { Thread.sleep( 1000 ); } catch (Exception ex) {}

		showAppletProgress( "Stopped" );

	}


	public void run()
	{
		Thread me = Thread.currentThread();
		
		// get the applet Graphics
		Graphics g = this.getGraphics();
		
		
		while( me == runner)
		{	
			// directly call update()
			update(g);
		}
	}


	public void paint(Graphics g)
	{
		setBackground( Color.black );
		g.setColor(Color.black);
		g.fillRect(0,0,width,height);
		g.setColor(Color.green);
		g.drawString(loadingMsg, 10, 20);	
	}
	

	public synchronized void update(Graphics g) 
	{
		//
		// update the frame
		//
		if (initialized)
		{
			// time elapsed since last frame
			long count;

			// update timing info (time may increase by 30ms per tick on some computers)
			NowMS=System.currentTimeMillis();
			count=NowMS-ThenMS;
			if(count>0)
			{
				ThenMS=NowMS;
				animate(count);
				render(doubleBufferGraphics);

				doubleBufferImage.flush();
				// draw double buffer
				g.drawImage(doubleBufferImage,0,0,this);
			}
		}
		else
		{
			paint(g);
			initialize();
		}
	}


	void animate(long count)
	{
		// Copy this since the input thread changes its value
		int myNewSizeX = newSizeX;
		int myNewSizeY = newSizeY;
		
		System.arraycopy(BlankPixels,0,TargetPixels,0,BlankPixels.length);
		
		// This puts the image behind the image there already
		//System.arraycopy(imagePixelArray,0,TargetPixels,0,imagePixelArray.length);

		
		//
		// update animations - positions of objects, effects parameters, etc.
		//
		int startX;
		int startY;
		int endX;
		int endY;
		int centerX = width/2;
		int centerY = height/2;
		startX = centerX - myNewSizeX/2;
		startY = centerY - myNewSizeY/2;
		endX = myNewSizeX + startX;
		endY = myNewSizeY + startY;
		int actualX, actualY;
		int indexTarget;
		int indexImage;
		
		
		for ( int y = startY; y < endY; y++ )
		{
			actualY = (( 2*(y - startY)*sizeY )/ ( 2*myNewSizeY ));
			for ( int x = startX; x < endX; x++ )
			{
				if ( ( x >= 0 && x < width ) && ( y >=0 && y < height ) )
				{
					actualX = (( 2*(x - startX)*sizeX )/ ( 2*myNewSizeX ));
					indexTarget = (x) + (y) * width;
					indexImage = actualX + actualY * sizeX;
				
					try 
					{
						int pixel;
						int overflow;
						
						// This mixes this pixel with the one below it
						pixel=(TargetPixels[indexTarget]&0xfefeff)+(imagePixelArray[ indexImage ]&0xfefeff);
						overflow=pixel&0x1010100;
						overflow=overflow-(overflow>>8);
						TargetPixels[ indexTarget ]=0xff000000|pixel|overflow;					
					}
					catch ( Exception ex )
					{
						System.out.println( "Help: " + ex );
					}
				}
			}
		}
	}


	void render(Graphics g)
	{
		//
		// draw everything
		//

		// fiddle around with pixel values in TargetPixels[] here

		TargetMIS.newPixels();
		g.drawImage(TargetImage,0,0,this);
	}

	
	public void initImages()
	{
 
		// Load any images here ( sample code provided )
		showAppletProgress( "Loading the image..." );

		
		// This is how to load the image
		// Load all of the images here
		Image storedImage = getImage(getCodeBase(), imageName);

		// Wait until the image has loaded
		while ((storedImage.getWidth(this))<0 || (storedImage.getHeight(this)<0)){}

		// This is the array to store pixels of an image
		// Create all the arrays to store the images here
		sizeX = storedImage.getWidth(this);
		sizeY = storedImage.getHeight(this);
		imagePixelArray = new int[ sizeX * sizeY ];		

		// Extract the pixels
		imagePixelArray = getPixels(storedImage,sizeX,sizeY);

		showAppletProgress( "...Image loaded" );

	}
	
	private int[] getPixels(Image TempImage, int width, int height)
	{
		int TempPixel[] = new int [width*height];

		PixelGrabber pg= new PixelGrabber(TempImage,0,0,width,height,TempPixel,0,width);
		try
		{
			pg.grabPixels();
		}
		catch (InterruptedException e)
		{
		}

		return TempPixel;
	}
	
	// Key Listener methods
	public void keyTyped(KeyEvent e)
	{
		//System.out.println( "keyTyped" );
	}

	public void keyPressed(KeyEvent e)
	{
	
		// don't do anything if applet not ready
		if ( !initialized ) 
		{
			return;
		}		
		
		
		keyCode = e.getKeyCode();
		
		// Do an action for the key.
		switch ( keyCode )
		{
		case KeyEvent.VK_ESCAPE:
			stop();
			break;
		case KeyEvent.VK_ADD:
			if ( newSizeX < maxImageSizeX && newSizeY < maxImageSizeY )
			{
				if ( newSizeX > sizeX )
				{
					changeX = (newSizeX/sizeX);
					changeX = changeX * changeX;
				}
				else
				{
					changeX = 1;
				}
				newSizeX = newSizeX + changeX;
			}			
			if ( newSizeY < maxImageSizeY )
			{
				if ( newSizeY > sizeY )
				{
					changeY = (newSizeY/sizeY);
					changeY = changeY * changeY;
				}
				else
				{
					changeY = 1;
				}
				newSizeY = newSizeY + changeY;
			}	
			showAppletProgress( "image size is ( " + newSizeX + ", "  + newSizeY + " )" );
			break;
		case KeyEvent.VK_SUBTRACT:
			if ( newSizeX > minImageSizeX && newSizeY > minImageSizeY )
			{
				if ( newSizeX > sizeX )
				{
					changeX = (newSizeX/sizeX);
					changeX = changeX * changeX;
				}
				else
				{
					changeX = 1;
				}
				newSizeX = newSizeX - changeX;
			}			
			if ( newSizeY > minImageSizeY )
			{
				if ( newSizeY > sizeY )
				{
					changeY = (newSizeY/sizeY);
					changeY = changeY * changeY;
				}
				else
				{
					changeY = 1;
				}
				newSizeY = newSizeY - changeY;
			}	
			showAppletProgress( "image size is ( " + newSizeX + ", "  + newSizeY + " )" );
			break;
		case KeyEvent.VK_UP:

			break;
		case KeyEvent.VK_DOWN:

			break;
		case KeyEvent.VK_LEFT:
			//System.out.println( "LEFT" );
			break;
		case KeyEvent.VK_RIGHT:
			//System.out.println( "RIGHT" );
			break;
		default:
			//System.out.println( "keyPressed" );
			break;
		}

	}

	public void keyReleased(KeyEvent e)
	{
		//System.out.println( "keyReleased" );
	}

	public boolean mouseDown(Event evt, int x, int y)
	{
		oldx=x;
		oldy=y;
		return true;
	}

	public boolean mouseDrag(Event evt, int x, int y)
	{
		oldx=x;
		oldy=y;
		
		// Get degrees of rotation in X and Y
		float rotationXRadians = (float)(oldx-x)*deg2rad;
		float rotationYRadians = (float)(y-oldy)*deg2rad;
		
		return true;
	}

	public boolean mouseUp(Event evt, int x, int y)
	{
		return true;
	}

	
	// This shows stuff on the status bar
	public void showAppletProgress( String msg )
	{
		getAppletContext().showStatus( "[ImageResize] " + msg + "..." );	
	}
}
