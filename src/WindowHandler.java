import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowHandler implements WindowListener {
	
	private final Game game;
	
	public WindowHandler(Game game) {
		this.game = game;
		StdDraw.frame.addWindowListener(this);
	}

	@Override
	public void windowActivated(WindowEvent event) {
		
	}

	@Override
	public void windowClosed(WindowEvent event) {
		
	}

	@Override
	public void windowClosing(WindowEvent event) {
		if (Game.player.currentTrade != null) {
			Packet016EndTrade endTradePacket = new Packet016EndTrade(Game.player.currentTrade.otherPlayer.name);
			endTradePacket.writeData(Game.socketClient);
		}
		
		//Game.savingPlayer = true;
		//while (Game.savingPlayer);
		
		Packet001Disconnect packet = new Packet001Disconnect(Game.player.name);
		packet.writeData(Game.socketClient);
	}

	@Override
	public void windowDeactivated(WindowEvent event) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent event) {
		
	}

	@Override
	public void windowIconified(WindowEvent event) {
		
	}

	@Override
	public void windowOpened(WindowEvent event) {
		
	}

}
