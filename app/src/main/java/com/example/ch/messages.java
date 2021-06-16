package com.example.ch;

public class messages {

    private String message, type, sender, push_id;
    private Boolean seen;


    public messages(String message, Boolean seen, String time, String type, String push_id) {
        this.message = message;
        this.seen = seen;
        //this.time = time;
        this.type = type;
        this.push_id=push_id;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String user_id) {
        this.sender = user_id;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

  //  public String getSeen() {
    //    return seen;
    //}

    //public void setSeen(String seen) {
      //  this.seen = seen;
    //}

    //public String getTime() {
      //  return time;
    //}

    //public void setTime(String time) {
      //  this.time = time;
    //}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public messages(){

   }




}
