/**
 *
 * The MIT License
 *
 * Copyright 2018-2022 Paul Conti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package builder.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.swing.JOptionPane;

import builder.parser.ParserException;
import builder.parser.Token;
import builder.parser.Tokenizer;

/**
 * The Class HexToImgConv reads in a C File containing an extern storage[] 
 * of Image Data and converts it to a BufferedImage.
 * Only supports one image per file and it should have been created
 * using my GUIslice_Image2C tool or UTFT tools ImgConv.exe, 
 * ImageConverter565.exe.  While other tools can create C Image files
 * the parser may not handle them correctly.
 * 
 * GUIslice_Image2C is available at:
 * https://github.com/Pconti31/GUIslice_Image2C
 * 
 * NOTE: Only GUIslice_Image2C version 2.00 or higher
 * converts Monographic bitmaps of 1 bit per pixel.
 * 
 * @author Paul Conti
 * 
 */
public class HexToImgConv {
  /** Token id for integers. */
  public static final int INTEGER                 = 1;
  
  /** Token id for floats. */
  public static final int HEX                     = 2;
  
  /** Token id for word. */
  public static final int WORD                    = 3;
  
  /** Token id for opening square bracket. */
  public static final int OPEN_SQBRACKET          = 4;
  
  /** Token id for opening brace. */
  public static final int OPEN_BRACE              = 5;
  
  /** Token id for comma. */
  public static final int COMMA                   = 6;
  
  /** Token id for semicolon. */
  public static final int SEMICOLON               = 7;
  
  /** Token id for special characters. */
  public static final int SPECIALCHAR             = 99;

  /** The Tokenizer. */
  private Tokenizer tokenizer;
  
  /** the extern name of C structure. */
  String externName;
  
  /** the nHeight of image. */
  int nHeight;
  
  /** the nWidth of image. */
  int nWidth;
  
  /** flag to indicate Monochrome bitmap */
  private boolean bbMonochrome;

  private Color colForeground;
  
  /**
   * Instantiates a new HexToImgConv.
   */
  public HexToImgConv() {
    tokenizer = new Tokenizer();
    // Create our tokenizer for C File Image parsing
    tokenizer.defineIgnored("\\s+"); // Ignore whitespace
    tokenizer.defineIgnored("^#.*$|^\\/\\/.*$"); // Ignore comments (which start with # or //)
    tokenizer.add("0x[a-fA-F0-9]+", HexToImgConv.HEX);
    tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*", HexToImgConv.WORD);
    tokenizer.add("[0-9]+", HexToImgConv.INTEGER);
    tokenizer.add("\\,", HexToImgConv.COMMA);
    tokenizer.add("\\[", HexToImgConv.OPEN_SQBRACKET);
    tokenizer.add("\\{", HexToImgConv.OPEN_BRACE);
    tokenizer.add("\\;", HexToImgConv.SEMICOLON);
    tokenizer.add(".", HexToImgConv.SPECIALCHAR);
  }
  
  /**
   * Do convert.
   *
   * @param file
   *          the file
   * @return the <code>buffered image</code> object
   */
  public BufferedImage doConvert(File file) {
    bbMonochrome = false;
    
    if (!file.getName().endsWith(".c")) {
      return null;
    }
      
    BufferedImage image = null;
    externName = null;
    int size = 0;
    nHeight = 0;
    nWidth = 0;
    Token token = null;
    try {
      tokenizer.setSource(file);
      
      // loop until we find we find 'const'
      while ((token = tokenizer.nextToken()).getToken() != null) {
        if (token.getType() == HexToImgConv.WORD &&
            token.getToken().equals("const"))
          break;
      }
      
      // pull out unsigned
      token = tokenizer.nextToken();
      if (token.getType() != HexToImgConv.WORD || 
          !token.getToken().equals("unsigned") ) parseError(token, "unsigned");
      token = tokenizer.nextToken();
      
      // now we get either short for 16 bit pixels or char for 1 bit pixels
      if (token.getType() != HexToImgConv.WORD || 
          !token.getToken().equals("short") ) {
        if (token.getToken().equals("char"))
          bbMonochrome = true;
        else
          parseError(token, "short");
      }
      
      // hopefully Token is our extern storage name
      token = tokenizer.nextToken();
      if (token.getType() != HexToImgConv.WORD) parseError(token, "storage_name[]");
      externName = token.getToken();
      // now figure out byte array size
      token = tokenizer.nextToken();
      if (token.getType() != HexToImgConv.OPEN_SQBRACKET) parseError(token, "[size of array]");
      token = tokenizer.nextToken();
      if (token.getType() == HexToImgConv.INTEGER) {
        size = Integer.valueOf(token.getToken());
        token = tokenizer.nextToken();
        if (token.getToken().equals("+"))
          size = size+2;
      } else if (token.getType() == HexToImgConv.HEX) {
        size = Integer.decode(token.getToken()).intValue();
        token = tokenizer.nextToken();
        if (token.getToken().equals("+"))
          size = size+2;
      } else {
        parseError(token, "[size of array]");
      }
      
      // skip pass until we get '{'
      while ((token = tokenizer.nextToken()).getType() != HexToImgConv.OPEN_BRACE) { }
      if (bbMonochrome) {
        int r= 0,g= 0,b= 0;
        // grab nHeight and nWidth next but each is in two bytes big endian format
        token = tokenizer.nextToken();
        // process height which may be in hex or decimal format
        if (token.getType() == HexToImgConv.HEX) {
          nHeight = Integer.decode(token.getToken()) << 8;
        } else if(token.getType() == HexToImgConv.INTEGER) {
          nHeight = Integer.valueOf(token.getToken()) << 8;
        } else parseError(token, "image nHeight");
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.COMMA) parseError(token, ",");
        token = tokenizer.nextToken();
        if (token.getType() == HexToImgConv.HEX) {
          nHeight |= Integer.decode(token.getToken());
        } else if(token.getType() == HexToImgConv.INTEGER) {
          nHeight |= Integer.valueOf(token.getToken());
        } else parseError(token, "image nHeight");
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.COMMA) parseError(token, ",");
        token = tokenizer.nextToken();
        // process width which may be in hex or decimal format
        if (token.getType() == HexToImgConv.HEX) {
          nWidth = Integer.decode(token.getToken()) << 8;
        } else if(token.getType() == HexToImgConv.INTEGER) {
          nWidth = Integer.valueOf(token.getToken()) << 8;
        } else parseError(token, "image nWidth");
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.COMMA) parseError(token, ",");
        token = tokenizer.nextToken();
        if (token.getType() == HexToImgConv.HEX) {
          nWidth |= Integer.decode(token.getToken());
        } else if(token.getType() == HexToImgConv.INTEGER) {
          nWidth |= Integer.valueOf(token.getToken());
        } else parseError(token, "image nWidth");
        // next set of values is the color of our foreground, red,green, and blue settings
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.COMMA) parseError(token, ",");
        token = tokenizer.nextToken();
        if (token.getType() == HexToImgConv.HEX) {
          r = Integer.decode(token.getToken());
        } else if(token.getType() == HexToImgConv.INTEGER) {
          r = Integer.valueOf(token.getToken());
        } else parseError(token, "foreground red color");
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.COMMA) parseError(token, ",");
        token = tokenizer.nextToken();
        if (token.getType() == HexToImgConv.HEX) {
          g = Integer.decode(token.getToken());
        } else if(token.getType() == HexToImgConv.INTEGER) {
          g = Integer.valueOf(token.getToken());
        } else  parseError(token, "foreground green color");
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.COMMA) parseError(token, ",");
        token = tokenizer.nextToken();
        if (token.getType() == HexToImgConv.HEX) {
          b = Integer.decode(token.getToken());
        } else if(token.getType() == HexToImgConv.INTEGER) {
          b = Integer.valueOf(token.getToken());
        } else  parseError(token, "foreground blue color");
        colForeground = new Color(r,g,b);
      } else {
        // grab nHeight and nWidth next
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.INTEGER) parseError(token, "image nHeight");
        nHeight = Integer.valueOf(token.getToken());
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.COMMA) parseError(token, ",");
        token = tokenizer.nextToken();
        if (token.getType() != HexToImgConv.INTEGER) parseError(token, "image nWidth");
        nWidth = Integer.valueOf(token.getToken());
      }
      
      // now build up our image inside a byte array
      int i = 0;
      int nRGB;
      byte[] bytes = new byte[(size-1)*2];
      if (!bbMonochrome) {
        byte hiByte, lowByte;
        while ((token = tokenizer.nextToken()).getType() != HexToImgConv.SEMICOLON) {
          if (token.getType() == HexToImgConv.HEX) {
            nRGB = Integer.decode(token.getToken());
            hiByte = (byte) ((nRGB & 0xFF00) >> 8);
            bytes[i++] = hiByte;
            lowByte = (byte)(nRGB & 0xFF);
            bytes[i++] = lowByte;
          }
        }
       tokenizer.close();
       ShortBuffer byteBuffer = ByteBuffer.wrap(bytes)
            .order(ByteOrder.BIG_ENDIAN) // Or LITTLE_ENDIAN depending on the spec of the card
            .asShortBuffer();            // Our data will be 16 bit unsigned shorts
        // Create a buffered image 
        image = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_USHORT_565_RGB);
        // Cast our input data to unsigned short, of course, Java doesn't make this easy
        // so we use the class (DataBufferUShort) which we don't even use except for the cast. 
        short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
        byteBuffer.get(data);
      } else {
        // for 1 bit pixels we only use unsigned char not short
        while ((token = tokenizer.nextToken()).getType() != HexToImgConv.SEMICOLON) {
          if (token.getType() == HexToImgConv.HEX) {
            nRGB = Integer.decode(token.getToken());
            bytes[i++] = (byte)(nRGB & 0xFF);;
          }
        }
        tokenizer.close();
        /* Here I would normally create a new indexed color map for a 1 bit pixel BMP
         * and set the background as transparent pixels so it will match what users 
         * will see on their TFT displays. I then load our bytes[] array into the raster 
         * of a new BufferedImage with type BufferedImage.TYPE_BYTE_BINARY.  
         * I did that and it works great with one major exception.
         * When we save the project we need to encode and decode the BMP file to
         * serialize it. This drops the fact we had a 1 bit pixel image with
         * transparent pixels and it gets converted to just a filled in square. Oops!
         * 
         * So now I create a BufferedImage.TYPE_INT_ARGB and set the background as
         * transparent by hand. This would be very slow for large files but the
         * monographic 1 bit files are all so small it won't nbe noticed.
         */
        // Create a buffered image 
        image = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = image.getRaster();
        
        /* now to the dirty work of pixel settings
         * each pixel is a single bit in our bytes[] array
         * and if a bit is 1 its foreground, 0 its background
         * so it needs to be transparent.
         */
        int bits = 0;
        /* We need to take padding into consideration
         * so calculate number of 8 bit bytes per row 
         */
        int byteWidth = (nWidth + 7) / 8; 
        int x;
        for (int y = 0; y < nHeight ; y++) {
          for (x = 0; x < nWidth; x++) {
            if((x & 7) > 0) {
              bits <<= 1;
            } else {
              bits = bytes[y * byteWidth + x / 8] & 0xFF;
            }
            if ((bits & 0x80) > 0) {
              raster.setPixel(x, y, new int[] { colForeground.getRed(), 
                                                colForeground.getGreen(), 
                                                colForeground.getBlue(), 
                                                0xFF});
            } else {
              raster.setPixel(x, y, new int[] { 0, 0, 0, 0 });
            }
          } // end x < nWidth
          
        } // end y < nHeight

        return image;
      }
      return image;
      } catch (IOException | ParserException | NumberFormatException e) {
        tokenizer.close();
        String msg = String.format("File '%s'\n'%s'\n", 
            file.getName(), e.toString());
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
        return null;
      }
  }
 
  /**
   * Gets the extern name.
   *
   * @return the extern name
   */
  public String getExternName() {
    return externName;
  }
  
  /**
   * Gets the width.
   *
   * @return the width
   */
  public int getWidth() {
    return nWidth;
  }
  
  /**
   * Gets the height.
   *
   * @return the height
   */
  public int getHeight() {
    return nHeight;
  }
  
  /**
   * Parses the error.
   *
   * @param t
   *          the t
   * @param s
   *          the s
   * @throws ParserException
   *           the parser exception
   */
  public void parseError(Token t, String s) throws ParserException {
    throw new ParserException("Missing '" + s + "' found <"+ t.getToken() +"> line: " + t.getLineNumber());
  }

}
