package com.selectamix.offline.tools;

import java.awt.*;
import java.io.File;
import java.io.RandomAccessFile;
/**
 * This type was generated by a SmartGuide.
 */
public class BuildMix2 extends Frame {

class IvjEventHandler implements java.awt.event.WindowListener {
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == BuildMix2.this) 
				connEtoC1(e);
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};
	private Panel ivjBuildMix2Pane = null;
	private Panel ivjContentsPane = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

//!!!! check val sizes: byte short int ????

  // length in bytes of each chunk
  final static int riffChunk_lengthVal = 12;
  final static int formatChunk_lengthVal = 24;
  final static int dataChunk_headerLengthVal = 8;
  
  // file pointers, in order of appearance in file

  //  RIFF chunk
  final static int riffChunk_startPtr = 0;
  final static int riffChunk_headerPtr = riffChunk_startPtr;
  final static int riffChunk_fileLengthMinusRiffHeaderPtr = riffChunk_startPtr + 4;
  final static int riffChunk_waveStringPtr = riffChunk_startPtr + 8;

  // FORMAT chunk
  final static int formatChunk_startPtr = riffChunk_startPtr + riffChunk_lengthVal;
  final static int formatChunk_headerStringPtr = formatChunk_startPtr; // this is the start address of the "fmt " string
  final static int formatChunk_lengthPtr = formatChunk_startPtr + 4;
  final static int formatChunk_encodingFormatPtr = formatChunk_startPtr + 8;
  final static int formatChunk_numberOfChannelsPtr = formatChunk_startPtr + 10;
  final static int formatChunk_sampleRatePtr = formatChunk_startPtr + 12;
  final static int formatChunk_bytesPerSecondPtr = formatChunk_startPtr + 16;
  final static int formatChunk_bytesPerSamplePtr = formatChunk_startPtr + 20;
  final static int formatChunk_bitsPerSamplePtr = formatChunk_startPtr + 22;

  // DATA chunk
  final static int dataChunk_startPtr = riffChunk_lengthVal + formatChunk_lengthVal;
  final static int dataChunk_headerStringPtr = dataChunk_startPtr; // this is the start address of the "data" string
  final static int dataChunk_sampleLengthInBytesPtr = dataChunk_startPtr + 4;
  final static int dataChunk_sampleStartPtr = dataChunk_startPtr + dataChunk_headerLengthVal;

  
  // numeric constants, in order of appearance
  final static int formatChunk_lengthMinusHeaderValAndLengthVal = 16;
  final static short formatChunk_encodingFormatVal = 0x01; // PCM format
  final static short formatChunk_numberOfChannelsForStereoVal = 2;
  final static int formatChunk_cdQualitySampleRateVal = 44100;
  final static int formatChunk_bytesPerChannelSampleVal = 2; // 16 bits per channel
  final static short formatChunk_bytesPerSampleVal =   formatChunk_bytesPerChannelSampleVal 
												     * formatChunk_numberOfChannelsForStereoVal;
  final static int formatChunk_bytesPerSecondForCdQualityVal =   formatChunk_bytesPerSampleVal 
															   * formatChunk_cdQualitySampleRateVal;
  final static short formatChunk_bitsPerSampleVal = formatChunk_bytesPerSampleVal * 8;
/**
 * BuildMix2 constructor comment.
 */
public BuildMix2() {
	super();
	initialize();
}
/**
 * BuildMix2 constructor comment.
 * @param title java.lang.String
 */
public BuildMix2(String title) {
	super(title);
}
/**
 * Insert the method's description here.
 * Creation date: (06/05/01 17:20:38)
 */
public void buildMix( File tune1File, File tune2File, File mixFile, File resultFile, int tune1Start, int tune2StartRelativeToTune1, int tune1MixOutStart, int tune2MixInEnd, int clipStart ) 
{
  try
  {
  // result
  RandomAccessFile result = new RandomAccessFile( resultFile, "rw" );	  
  // open the files, so they're ready to be read
  RandomAccessFile tune1 = new RandomAccessFile( tune1File, "r" );
  RandomAccessFile tune2 = new RandomAccessFile( tune2File, "r" );
  RandomAccessFile mix =  new RandomAccessFile( mixFile, "r" );



  System.out.println("here");
  // result RIFF

  // initialise result file riff constant fields
  result.seek( riffChunk_headerPtr );
  result.writeInt( 0x52494646 ); // ascii "RIFF"
  result.seek( riffChunk_waveStringPtr );
  result.writeInt( 0x57415645 ); // ascii "WAVE"

  // initialise result file riff calculated fields
  
  // work out length that result will be and write it to appropriate fields in riff, format and data chunks in its header

  tune1.seek( dataChunk_sampleLengthInBytesPtr );
  int tune1SampleLengthInBytes = littleEndianReadInt( tune1 );

  tune2.seek( dataChunk_sampleLengthInBytesPtr );
  int tune2SampleLengthInBytes = littleEndianReadInt( tune2 );

  mix.seek( dataChunk_sampleLengthInBytesPtr );
  int mixSampleLengthInBytes = littleEndianReadInt( mix );
  
  int lengthOfTune1Used = tune1MixOutStart - 1; // 1 sample before mix starts
  int lengthOfMixUsed = tune2MixInEnd - tune1MixOutStart;
  int lengthOfTune2Used = tune2SampleLengthInBytes - tune2MixInEnd;

  int numberOfSamples = lengthOfTune1Used + lengthOfMixUsed + lengthOfTune2Used;
  int numberOfSamplesInBytes = numberOfSamples * formatChunk_bytesPerSampleVal;
  
  // write this to result
  result.seek( riffChunk_fileLengthMinusRiffHeaderPtr );
  littleEndianWrite(   riffChunk_lengthVal - 4
	                 + formatChunk_lengthVal
	                 + dataChunk_headerLengthVal
	                 + numberOfSamplesInBytes, result );
  result.seek( dataChunk_sampleLengthInBytesPtr );
  littleEndianWrite( numberOfSamplesInBytes, result );

  // result FORMAT

  result.seek( formatChunk_headerStringPtr );
  // write "fmt " ??????
  result.seek( formatChunk_lengthPtr );
  littleEndianWrite( 0x0010, result );
  result.seek( formatChunk_numberOfChannelsPtr );
  littleEndianWrite( formatChunk_numberOfChannelsForStereoVal, result );
  result.seek( formatChunk_sampleRatePtr );
  littleEndianWrite( formatChunk_cdQualitySampleRateVal, result );
  result.seek( formatChunk_bytesPerSecondPtr );
  littleEndianWrite( formatChunk_bytesPerSecondForCdQualityVal, result );
  result.seek( formatChunk_bytesPerSamplePtr );
  littleEndianWrite( formatChunk_bytesPerSampleVal, result );
  result.seek( formatChunk_bitsPerSamplePtr );
  littleEndianWrite( formatChunk_bitsPerSampleVal, result );

  // DATA

  result.seek( dataChunk_headerStringPtr );
  result.writeInt( 0x64617464 ); // ascii "data"
  
  System.out.println("tune1");
   
  // read tune 1 up to where mix starts
  // write tune 1 up to where mix starts to result
  // 1 sample is integer
  tune1.seek( dataChunk_sampleStartPtr );
  for ( int samples = 0; samples < tune1MixOutStart; samples++ )
  {
	result.writeInt( tune1.readInt() );
  }
  // close tune 1
  tune1.close();

  System.out.println("mix");
  
  // open mix
  // read mix to end
  // write mix to end to result file
  // close mix
  int relativeTune1MixOutStart = tune1MixOutStart - clipStart;
  int mixEnd = tune2MixInEnd - clipStart; // faulty
  mix.seek( dataChunk_sampleStartPtr + (relativeTune1MixOutStart * formatChunk_bytesPerSampleVal) );
  for( int samples = 0; samples < mixEnd; samples++ )
  {
	result.writeInt( mix.readInt() );
  }
  mix.close();  

  System.out.println("tune2");
  
  // read tune 2 from where mix ends
  // write tune 2 from where mix ends to result
  int relativeTune2Start = tune2MixInEnd - tune2StartRelativeToTune1;
  mix.seek( dataChunk_sampleStartPtr + (relativeTune2Start * formatChunk_bytesPerSampleVal) );
  int tune2SampleLength = tune2SampleLengthInBytes / formatChunk_bytesPerSampleVal;
  for (int samples = 0; samples < tune2SampleLengthInBytes; samples++ )
  {
	result.writeInt( tune2.readInt() );
  }
  tune2.close();
  // close tune 2

  // close result
  result.close();
  }
  catch( java.io.IOException e )
  {
	System.out.println("buildMix");
	/*
	try 
	{
		
	  result.close();
	  // open the files, so they're ready to be read
	  tune1.close();
	  tune2.close();
	  mix.close();
	   
	} catch ( java.io.IOException e2 ) {};
	*/
  }
}
/**
 * connEtoC1:  (BuildMix2.window.windowClosing(java.awt.event.WindowEvent) --> BuildMix2.dispose()V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the BuildMix2Pane property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getBuildMix2Pane() {
	if (ivjBuildMix2Pane == null) {
		try {
			ivjBuildMix2Pane = new java.awt.Panel();
			ivjBuildMix2Pane.setName("BuildMix2Pane");
			ivjBuildMix2Pane.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBuildMix2Pane;
}
/**
 * Return the ContentsPane property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new java.awt.Panel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.BorderLayout());
			getContentsPane().add(getBuildMix2Pane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addWindowListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BuildMix2");
		setLayout(new java.awt.BorderLayout());
		setSize(460, 300);
		setTitle("BuildMix2");
		add(getContentsPane(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (06/05/01 22:06:24)
 * @param value int
 */
public int littleEndianReadInt(RandomAccessFile file ) 
{
  int bigEndianValue = 0;
  try
  {
	bigEndianValue =    ((int)file.readByte())
	                 + (((int)file.readByte()) >> 8)
	                 + (((int)file.readByte()) >> 16)
	                 + (((int)file.readByte()) >> 24);
  }
  catch( java.io.IOException e )
  {
	System.out.println("public int littleEndianReadInt(RandomAccessFile file )");
	try { file.close(); } catch ( java.io.IOException e2 ) {};
  }

  return bigEndianValue;
}
/**
 * Insert the method's description here.
 * Creation date: (06/05/01 22:06:24)
 * @param value int
 */
public int littleEndianReadShort(RandomAccessFile file ) 
{
  int bigEndianValue = 0;
  try
  {
	bigEndianValue =    (file.readByte())
	                 + ((file.readByte()) >> 8);
  }
  catch( java.io.IOException e )
  {
	System.out.println("public int littleEndianReadShort(RandomAccessFile file )");
	try { file.close(); } catch ( java.io.IOException e2 ) {};
  }

  return bigEndianValue;
}
/**
 * Insert the method's description here.
 * Creation date: (06/05/01 22:06:24)
 * @param value int
 */
public void littleEndianWrite(int value, RandomAccessFile file ) 
{
  try
  {
	file.writeByte( (byte) value );
	file.writeByte( (byte) value >> 8 );
	file.writeByte( (byte) value >> 16 );
	file.writeByte( (byte) value >> 24 );
  }
  catch( java.io.IOException e )
  {
	System.out.println("public void littleEndianWrite(int value, RandomAccessFile file )");
	try { file.close(); } catch ( java.io.IOException e2 ) {};
  }
}
/**
 * Insert the method's description here.
 * Creation date: (06/05/01 22:06:24)
 * @param value int
 */
public void littleEndianWrite(short value, RandomAccessFile file ) 
{
  try
  {
	file.writeByte( (byte) value );
	file.writeByte( (byte) value >> 8 );
  }
  catch( java.io.IOException e )
  {
	System.out.println("public void littleEndianWrite(short value, RandomAccessFile file ) ");
	try { file.close(); } catch ( java.io.IOException e2 ) {};
  }
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		/* Create the frame */
		BuildMix2 aBuildMix2 = new BuildMix2();
		/* Add a windowListener for the windowClosedEvent */
		aBuildMix2.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aBuildMix2.setVisible(true);
		aBuildMix2.run();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of BuildMix2");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (06/05/01 17:16:41)
 */
public void run() 
{
File mix = new File( "D:\\VST Audio\\CEP\\selectamix\\demo\\uk garage\\chopped", "big love to on the run 2.wav" );
File tune1 = new File( "D:\\VST Audio\\CEP\\selectamix\\demo\\uk garage\\mixes", "biglove 127.9bpm.wav");
File tune2 = new File( "D:\\VST Audio\\CEP\\selectamix\\demo\\uk garage\\mixes", "ontherun 127.9bpm.wav" );
File result = new File( "D:\\", "mix.wav" );
//File result = new File( "D:/VST Audio/CEP/selectamix/demo/uk garage/result", "mix.wav" );
buildMix(  tune1, tune2, mix, result, 0, 6552137, 6512230, 8412032, 6478313 );

}
}
