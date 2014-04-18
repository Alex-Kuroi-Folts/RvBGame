package com.me.rvbgame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.me.rvbgame.screens.BattleScreen;

public abstract class RvBUnit extends RvBBase {

	private String unitName = "RvBUnit";
	
    private int health;
    private int energy;
    private int maxHealth;

    private int pAttack;    //physical attack
    private int pDefence;   //physical defence

    private int iAttack;    //intelligence attack
    private int iDefence;   //intelligence defence

    private byte targetsNum;
    private byte attackRange;

    private int criticalChance;
    private int actionPoints;
    private boolean bReachable;

    public int getMinActionPoints() {
        return minActionPoints;
    }

    protected int minActionPoints;

    public UnitType unitType;
    public ActionType actionType;

    protected RvBPlayer player;
    private boolean bCanOperate;
    private boolean bDead;
    public boolean bFreeze;

    protected String avaPath = "data/Andr_087098.png";
	
	protected Image avaImage;
    protected Label healthLeftLabel;
	private Texture avaTexture;
	protected int avaTexWidth = 128;
	protected int avaTexHeight = 128;
	protected Vector2 avaSize = new Vector2(48, 48);
    private boolean defended;
    public int line;

    public RvBUnit(BattleScreen parentScreen, RvBPlayer playerOwner, String jsonData) {
        super(parentScreen);
        
        player = playerOwner;
        
        Json js = new Json();
        
        RvBUnit tmpUnit = js.fromJson(RvBTower.class, Gdx.files.internal(jsonData));
        if (tmpUnit != null)
        {
        	this.unitName = tmpUnit.getUnitName();
        	
        	this.setHealth(tmpUnit.getHealth());
        	//this.setEnergy(tmpUnit.getEnergy());
        	this.energy = tmpUnit.getEnergy();
        	
        	this.setpAttack(tmpUnit.getpAttack());
        	this.setpDefence(tmpUnit.getpDefence());
        	
        	this.setiAttack(tmpUnit.getiAttack());
        	this.setiDefence(tmpUnit.getiDefence());
        	
        	this.setTargetsNum(tmpUnit.getTargetsNum());
        	this.setAttackRange(tmpUnit.getAttackRange());
        	
        	this.avaPath = tmpUnit.avaPath;
        	this.avaTexWidth = tmpUnit.avaTexWidth;
        	this.avaTexHeight = tmpUnit.avaTexHeight;
        	this.avaSize = tmpUnit.avaSize;
            this.healthLeftLabel = new Label(String.format("%d", tmpUnit.getHealth()),battleScreen.getSkin());
            this.healthLeftLabel.setColor(Color.GREEN);
            this.healthLeftLabel.setAlignment(Align.center, Align.center);
            this.maxHealth = tmpUnit.getHealth();
            this.actionType = ActionType.ACTION_TYPE_DONE;
            if (tmpUnit.getCriticalChance() > 0){
                this.criticalChance = tmpUnit.getCriticalChance();
            }
            this.actionPoints = tmpUnit.actionPoints;
            this.minActionPoints = tmpUnit.actionPoints;

        }
    }
	
    public RvBUnit(BattleScreen parentScreen, RvBPlayer playerOwner) {
        super(parentScreen);
        
        player = playerOwner;
    }

    public RvBUnit() {
		super();
	}
    
    public String getImagePath() {
		return avaPath;
	}
    
    public int getAvaImageWidth() {
		return avaTexWidth;
	}
    
    public int getAvaImageHeight() {
		return avaTexHeight;
	}
    
    public Vector2 getAvaSize() {
		return avaSize;
	}


    public static boolean IsPhysAttack(int attackID) {
		return true;
	}
    
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if(healthLeftLabel!=null){
            healthLeftLabel.setText(String.format("%d",health));
            if (health<maxHealth && health>maxHealth/2) healthLeftLabel.setColor(Color.YELLOW);
            else if (health!=maxHealth) healthLeftLabel.setColor(Color.RED);
        }
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        if (energy<0) energy = 0;
        if (battleScreen.world != null)
            battleScreen.world.updateStatLabels(this);
        this.energy = energy;
    }

    public int getpAttack() {
        return pAttack;
    }

    public void setpAttack(int pAttack) {
        this.pAttack = pAttack;
    }

    public int getpDefence() {
        return pDefence;
    }

    public void setpDefence(int pDefence) {
        this.pDefence = pDefence;
    }

    public int getiAttack() {
        return iAttack;
    }

    public void setiAttack(int iAttack) {
        this.iAttack = iAttack;
    }

    public int getiDefence() {
        return iDefence;
    }

    public void setiDefence(int iDefence) {
        this.iDefence = iDefence;
    }

    public byte getTargetsNum() {
        return targetsNum;
    }

    public void setTargetsNum(byte targetsNum) {
        this.targetsNum = targetsNum;
    }

    public byte getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(byte attackRange) {
        this.attackRange = attackRange;
    }

	public boolean isbCanOperate() {
		return bCanOperate;
	}

	public void setbCanOperate(boolean bCanOperate) {
		this.bCanOperate = bCanOperate;

//		if (bFreeze && !bCanOperate)
//            this.unFreeze();

        if (!bCanOperate && !bFreeze)
		{
			settowerColor(Color.DARK_GRAY);
		} else if (!bFreeze)
		{
			settowerColor(new Color(1, 1, 1, 1));
		}
		Gdx.app.log("RvB", "setbCanOperate "+bCanOperate);
	}



    public boolean isbDead() {
		return bDead;
	}

	public void setbDead(boolean bDead) {
		this.bDead = bDead;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (avaTexture != null)
		{
			avaTexture.dispose();
		}
	}

	public void settowerColor(Color newColor)
	{
		if (avaImage != null)
		{
			avaImage.setColor(newColor);
		}
	}
	
	@Override
	public void show() {
		super.show();
		
		addToDraw();
	}
	
	//unused
	@Override
	public void addToDraw() {
		super.addToDraw();
		
		avaTexture = new Texture(Gdx.files.internal(getImagePath()));
		avaTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(avaTexture, 0, 0, getAvaImageWidth(), getAvaImageHeight());
		
		avaImage = new Image(region);
		avaImage.setScaling(Scaling.stretch);
		avaImage.setAlign((Align.bottom | Align.left));
		avaImage.setSize(getAvaSize().x, getAvaSize().y);
        avaImage.setColor(Color.DARK_GRAY);
		
		avaImage.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//show radial menu
                if (RvBWorld.getCurrentTurnPlayer() == player)
				{
                    if (RvBWorld.getCurrentTurnPlayer().getActingUnit()!=null) {
                        if (RvBWorld.getCurrentTurnPlayer().getActingUnit().actionType == ActionType.ACTION_TYPE_HEAL)
                        {
                            if (RvBWorld.heal(RvBWorld.getCurrentTurnPlayer().getActingUnit(),RvBUnit.this,0)){
//                                RvBWorld.getCurrentTurnPlayer().clearSelection();
//                                battleScreen.world.updateStatLabels(RvBWorld.getCurrentTurnPlayer().getActingUnit());
                                RvBWorld.getCurrentTurnPlayer().getActingUnit().setActionPoints(RvBWorld.getCurrentTurnPlayer().getActingUnit().getActionPoints()-1);
                                if (RvBWorld.getCurrentTurnPlayer().getActingUnit().getActionPoints() == 0){

                                    RvBWorld.getCurrentTurnPlayer().getActingUnit().setbCanOperate(false);
                                    RvBWorld.getCurrentTurnPlayer().getActingUnit().actionType = ActionType.ACTION_TYPE_DONE;
                                    if (!RvBWorld.getCurrentTurnPlayer().checkIfCanMove()){
                                        RvBWorld.getCurrentTurnPlayer().endTurn();
                                    }
                                }
                                battleScreen.sceneLayerRadialMenu.setDefaultColors();
                                battleScreen.sceneLayerRadialMenu.hide();
                                return;
                            };
                        }
                    }

                    if (!RvBUnit.this.bFreeze && RvBUnit.this.bCanOperate){
//                        RvBWorld.getOppositePlayer().fillAvailableVictims( RvBUnit.this,RvBUnit.this.getAttackRange());
                        player.setActingUnit(RvBUnit.this);
                        RvBWorld.getOppositePlayer().fillAvailableVictims( RvBUnit.this.getAttackRange() - RvBWorld.getCurrentTurnPlayer().numOfLinesInFront(RvBUnit.this));
                    }
				}
                else
                //or apply selected action on victim
				{
					if (RvBWorld.getCurrentTurnPlayer().getActingUnit() != null && RvBWorld.getCurrentTurnPlayer().getActingUnit().actionType != ActionType.ACTION_TYPE_HEAL)
					{
                        if(RvBWorld.applyActionOnVictim(RvBWorld.getCurrentTurnPlayer().getActingUnit(), RvBUnit.this));
						{
                            if (RvBWorld.getCurrentTurnPlayer().getActingUnit().getActionPoints() == 0){

                                RvBWorld.getCurrentTurnPlayer().getActingUnit().setbCanOperate(false);
                                RvBWorld.getCurrentTurnPlayer().getActingUnit().actionType = ActionType.ACTION_TYPE_DONE;
                                if (!RvBWorld.getCurrentTurnPlayer().checkIfCanMove()){
                                    RvBWorld.getCurrentTurnPlayer().endTurn();
                                }
                            }
                            battleScreen.sceneLayerRadialMenu.setDefaultColors();
                            battleScreen.sceneLayerRadialMenu.hide();
						}
					}
				}
			};
		});	

		switch (unitType)
		{
			case UNIT_TYPE_TOWER:
				battleScreen.sceneLayerTowers.addActor(avaImage);
                battleScreen.sceneLayerTowers.addActor(healthLeftLabel);
				break;
	
			default:
				battleScreen.sceneLayerUnits.addActor(avaImage);
                battleScreen.sceneLayerUnits.addActor(healthLeftLabel);
                break;
		}
		
		if (unitType != UnitType.UNIT_TYPE_TOWER)
		{
	    	if (player == battleScreen.world.playerRight)
	    	{
	    		if (player.slot1 == this)
	    		{
	    			avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT01.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT01.y - (avaImage.getHeight() * 0.5f));
                } else
				if (player.slot2 == this)
				{
					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT02.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT02.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot3 == this)
				{
					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT03.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT03.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot4 == this)
				{
					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT04.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT04.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot5 == this)
	    		{
					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT05.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT05.y - (avaImage.getHeight() * 0.5f));
	    		}
	    	} else
	    	{
	    		if (player.slot1 == this)
	    		{
	    			avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT01.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT01.y - (avaImage.getHeight() * 0.5f));
	    		} else 
				if (player.slot2 == this)
				{
					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT02.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT02.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot3 == this)
				{
					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT03.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT03.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot4 == this)
				{
					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT04.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT04.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot5 == this)
	    		{
					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT05.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT05.y - (avaImage.getHeight() * 0.5f));
	    		}
	    	}
	    }

        healthLeftLabel.setPosition(avaImage.getX(),avaImage.getY());
    }
	
	@Override
	public void removeFromDraw() {
		super.removeFromDraw();
		
		switch (unitType)
		{
			case UNIT_TYPE_TOWER:
				battleScreen.sceneLayerTowers.removeActor(avaImage);
                battleScreen.sceneLayerTowers.removeActor(healthLeftLabel);
				break;
	
			default:
				battleScreen.sceneLayerUnits.removeActor(avaImage);
                battleScreen.sceneLayerUnits.removeActor(healthLeftLabel);
				break;
		}
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		if (unitType != UnitType.UNIT_TYPE_TOWER)
		{
    		float deltaWidthCoef = (battleScreen.screenResW / RvBWorld.WORLD_NATIVE_RES.x);
    		float deltaHeightCoef = (battleScreen.screenResH / RvBWorld.WORLD_NATIVE_RES.y);
    		float deltaAvaSizeW = getAvaSize().x * deltaHeightCoef;
	    	if (player == battleScreen.world.playerRight)
	    	{
	    		if (player.slot1 == this)
	    		{
	    			avaImage.setPosition((RvBWorld.RIGHT_UNIT_SLOT01.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.RIGHT_UNIT_SLOT01.y * deltaHeightCoef);
//	    			avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT01.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT01.y - (avaImage.getHeight() * 0.5f));
	    		} else 
				if (player.slot2 == this)
				{
					avaImage.setPosition((RvBWorld.RIGHT_UNIT_SLOT02.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.RIGHT_UNIT_SLOT02.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT02.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT02.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot3 == this)
				{
					avaImage.setPosition((RvBWorld.RIGHT_UNIT_SLOT03.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.RIGHT_UNIT_SLOT03.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT03.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT03.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot4 == this)
				{
					avaImage.setPosition((RvBWorld.RIGHT_UNIT_SLOT04.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.RIGHT_UNIT_SLOT04.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT04.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT04.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot5 == this)
	    		{
					avaImage.setPosition((RvBWorld.RIGHT_UNIT_SLOT05.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.RIGHT_UNIT_SLOT05.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.RIGHT_UNIT_SLOT05.x - (avaImage.getWidth() * 0.5f), RvBWorld.RIGHT_UNIT_SLOT05.y - (avaImage.getHeight() * 0.5f));
	    		}
	    	} else
	    	{
	    		if (player.slot1 == this)
	    		{
	    			avaImage.setPosition((RvBWorld.LEFT_UNIT_SLOT01.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.LEFT_UNIT_SLOT01.y * deltaHeightCoef);
//	    			avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT01.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT01.y - (avaImage.getHeight() * 0.5f));
	    		} else 
				if (player.slot2 == this)
				{
					avaImage.setPosition((RvBWorld.LEFT_UNIT_SLOT02.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.LEFT_UNIT_SLOT02.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT02.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT02.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot3 == this)
				{
					avaImage.setPosition((RvBWorld.LEFT_UNIT_SLOT03.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.LEFT_UNIT_SLOT03.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT03.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT03.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot4 == this)
				{
					avaImage.setPosition((RvBWorld.LEFT_UNIT_SLOT04.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.LEFT_UNIT_SLOT04.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT04.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT04.y - (avaImage.getHeight() * 0.5f));
				} else 
				if (player.slot5 == this)
	    		{
					avaImage.setPosition((RvBWorld.LEFT_UNIT_SLOT05.x * deltaWidthCoef * (1.0f + ((deltaAvaSizeW - getAvaSize().x) / battleScreen.screenResW))), RvBWorld.LEFT_UNIT_SLOT05.y * deltaHeightCoef);
//					avaImage.setPosition(RvBWorld.LEFT_UNIT_SLOT05.x - (avaImage.getWidth() * 0.5f), RvBWorld.LEFT_UNIT_SLOT05.y - (avaImage.getHeight() * 0.5f));
	    		}
	    	}
	    	avaImage.setSize(deltaAvaSizeW, deltaAvaSizeW);
            avaImage.setColor(Color.DARK_GRAY);

            healthLeftLabel.setPosition(avaImage.getX(),avaImage.getY());
	    }
	}

    public void freeze() {
        this.bFreeze = true;
        this.avaImage.setColor(StatsHelper.COLOR_DARK_BLUE);
    }
    public void unFreeze() {
        this.bFreeze = false;
        this.avaImage.setColor(Color.DARK_GRAY);
    }

    public void setDefended(boolean defended) {
        this.defended = defended;
    }

    public boolean isDefended() {
        return defended;
    }

    public int getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(int criticalChance) {
        this.criticalChance = criticalChance;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public void setActionPoints(int actionPoints) {
        this.actionPoints = actionPoints;
    }

    public void setbReachable(boolean bReachable) {
        if (!bReachable)
            this.settowerColor(Color.DARK_GRAY);
        this.bReachable = bReachable;
    }

    public boolean isbReachable() {
        return bReachable;
    }

	public String getUnitName() {
		return unitName;
	}

    public int getMaxHealth() {
        return maxHealth;
    }
}
