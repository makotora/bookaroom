package com.bookaroom.models;

public class SendMessageRequest
{
    private Long conversationId;
    private String message;

    public SendMessageRequest(Long conversationId, String message)
    {
        super();
        this.conversationId = conversationId;
        this.message = message;
    }

    public Long getConversationId()
    {
        return conversationId;
    }

    public void setConversationId(Long conversationId)
    {
        this.conversationId = conversationId;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "SendMessageRequest [conversationId=" + conversationId + ", message=" + message + "]";
    }

}
