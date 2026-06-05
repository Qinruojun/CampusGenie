        if isinstance(answer, dict):
            return ChatResponse(
                answer=answer["answer"],
                cost_time=cost_time,
                knowledge_id=answer.get("kb_id")
                )
        else:
            return ChatResponse(answer=answer, cost_time=cost_time)