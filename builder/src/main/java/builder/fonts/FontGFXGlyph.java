package builder.fonts;

public class FontGFXGlyph {
  public int bitmapOffset;  ///< Index into our giant GFXfont->bitmap
  public int width;         ///< Bitmap dimensions in pixels
  public int height;        ///< Bitmap dimensions in pixels
  public int xAdvance;      ///< Distance to advance cursor (x axis)
  public int xOffset;        ///< X dist from cursor pos to UL corner
  public int yOffset;        ///< Y dist from cursor pos to UL corner

}
