import java.io.Serializable;

public class Quests implements Serializable {
	
	public final Player player;
	
	public static enum QuestList {
		NONE				(0);
		
		private int questId;
		private QuestList(int questId) {
			this.questId = questId;
		}
		
		public int getId() {
			return questId;
		}
	}
	
	public int currentQuest = 0; 
	
	public Quests(Player player) {
		this.player = player;
	}
	
	public String getQuestName(int quest) {
		
		QuestList questId = null;
		for (QuestList q : QuestList.values()) {
			if (q.getId() == quest) {
				questId = q;
				break;
			}
		}
		
		if (questId == null) return "ERROR: could not find quest";
		
		switch (questId) {
		
		case NONE:
			return "None";
			
		}
		
		return "None";
	}
	
	public String getCurrentObjective(int quest) {
		
		QuestList questId = null;
		for (QuestList q : QuestList.values()) {
			if (q.getId() == quest) {
				questId = q;
				break;
			}
		}
		
		if (questId == null) return "ERROR: could not find current quest objective";
		
		switch (questId) {
		
		case NONE:
			return "None";
			
		}
		
		return "ERROR: could not find current quest objective";
	}

}
