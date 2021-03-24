package sdp.moneyrun;

class Riddle{
    private String question;
    private String answer;

    public Riddle(String question, String answer){
        if(question == null || answer == null)
            throw new IllegalArgumentException("Null arguments in sdp.moneyrun.Riddle constructor");
        this.question = question;
        this.answer = answer;
    }
    public String getQuestion(){
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}

