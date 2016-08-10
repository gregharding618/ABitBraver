
public class PathNode {

	public double Hcost = 0;
	public double Gcost = 0;
	public double Fcost = 0;
	public double mapX = 0;
	public double mapY = 0;
	
	public PathNode(double Gcost, double Hcost, double mapX, double mapY){
		this.Gcost = Gcost;
		this.Hcost = Hcost;
		Fcost = Gcost + Hcost;
		
		this.mapX = mapX;
		this.mapY = mapY;
	}
}
