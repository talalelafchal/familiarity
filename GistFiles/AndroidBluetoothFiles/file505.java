package com.ortiz.sangredeportiva;

public class Positions {
	
	private String pos, name, points, played, win, draw, lost, goals_score, goals_conc;

	public Positions(String pos, String name, String points, String played,
			String win, String draw, String lost, String goals_score,
			String goals_conc) {
		super();
		this.pos = pos;
		this.name = name;
		this.points = points;
		this.played = played;
		this.win = win;
		this.draw = draw;
		this.lost = lost;
		this.goals_score = goals_score;
		this.goals_conc = goals_conc;
	}

	
	public String getPos() {
		return pos;
	}

	
	public void setPos(String pos) {
		this.pos = pos;
	}

	
	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public String getPoints() {
		return points;
	}

	
	public void setPoints(String points) {
		this.points = points;
	}

	
	public String getPlayed() {
		return played;
	}

	
	public void setPlayed(String played) {
		this.played = played;
	}

	public String getWin() {
		return win;
	}

	public void setWin(String win) {
		this.win = win;
	}

	
	public String getDraw() {
		return draw;
	}

	
	public void setDraw(String draw) {
		this.draw = draw;
	}

	
	public String getLost() {
		return lost;
	}

	
	public void setLost(String lost) {
		this.lost = lost;
	}

	
	public String getGoals_score() {
		return goals_score;
	}

	
	public void setGoals_score(String goals_score) {
		this.goals_score = goals_score;
	}

	
	public String getGoals_conc() {
		return goals_conc;
	}

	
	public void setGoals_conc(String goals_conc) {
		this.goals_conc = goals_conc;
	}

}
