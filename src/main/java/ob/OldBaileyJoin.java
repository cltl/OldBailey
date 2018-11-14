package ob;

public class OldBaileyJoin {
    /*
    <join result="persNamePlace" targOrder="Y"
                                  targets="OA16980309n24-1 n24-3 OA16980309-geo-1"/>Aldgate Parish</placeName>


      <join result="persNameOccupation" targOrder="Y"
                            targets="OA16980309n37-1 OA16980309-occupation-4"/>.</p>
     */

     private String result;
     private String targetOrder;
     private String subject;
     private String object;

    public OldBaileyJoin(String result, String targetOrder, String subject, String object) {
        this.result = result;
        this.targetOrder = targetOrder;
        this.subject = subject;
        this.object = object;
    }

    public OldBaileyJoin() {
        this.result = "";
        this.targetOrder = "";
        this.subject = "";
        this.object = "";
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTargetOrder() {
        return targetOrder;
    }

    public void setTargetOrder(String targetOrder) {
        this.targetOrder = targetOrder;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
