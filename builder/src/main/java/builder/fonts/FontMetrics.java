package builder.fonts;

public class FontMetrics {
  public int  x1;  // The boundary X coordinate
  public int  y1;  // The boundary Y coordinate
  public int  w;  // The boundary width
  public int  h;  // The boundary height
  
  
  public FontMetrics() {
    this.x1 = 0;
    this.y1 = 0;
    this.w = 0;
    this.h = 0;
  }
  
  public FontMetrics(int x1, int y1, int w, int h) {
     this.x1 = x1;
     this.y1 = y1;
     this.w = w;
     this.h = h;
  }

}
