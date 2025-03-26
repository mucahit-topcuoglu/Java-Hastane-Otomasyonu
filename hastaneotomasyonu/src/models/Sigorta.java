package models;

public class Sigorta  extends Model{
   
    private int hastaId;
    private String sigortaTuru;
    private String sigortaDetayi;

    public Sigorta(int id, int hastaId, String sigortaTuru, String sigortaDetayi) {
        super(id);
        this.hastaId = hastaId;
        this.sigortaTuru = sigortaTuru;
        this.sigortaDetayi = sigortaDetayi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getHastaId() { return hastaId; }
    public void setHastaId(int hastaId) { this.hastaId = hastaId; }
    
    public String getSigortaTuru() { return sigortaTuru; }
    public void setSigortaTuru(String sigortaTuru) { this.sigortaTuru = sigortaTuru; }
    
    public String getSigortaDetayi() { return sigortaDetayi; }
    public void setSigortaDetayi(String sigortaDetayi) { this.sigortaDetayi = sigortaDetayi; }
} 