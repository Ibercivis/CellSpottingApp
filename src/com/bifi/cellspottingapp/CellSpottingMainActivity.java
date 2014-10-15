package com.bifi.cellspottingapp;

import java.util.ArrayList;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

/*
 * CellSpottingMainActivity
 * -------------------------
 * 
 * Author: Eduardo Lostal
 * 
 */
public class CellSpottingMainActivity extends ActionBarActivity {

	ImageView progressBarView;
	Bitmap bitmap;  
	
	// Stores the id of the current image under analysis
	// Note it starts in zero
	int currentImage;
	
	int[] images = {R.drawable.cells1, R.drawable.cells2, R.drawable.cells3, R.drawable.cells4, 
			R.drawable.cells5, R.drawable.cells6, R.drawable.cells7, R.drawable.cells8, R.drawable.cells9, 
			R.drawable.cells10, R.drawable.cells11, R.drawable.cells12, R.drawable.cells13, R.drawable.cells14, 
			R.drawable.cells15, R.drawable.cells16, R.drawable.cells17, R.drawable.cells18, R.drawable.cells19, 
			R.drawable.cells20, R.drawable.cells21, R.drawable.cells22, R.drawable.cells23, R.drawable.cells24, 
			R.drawable.cells25, R.drawable.cells26, R.drawable.cells27, R.drawable.cells28, R.drawable.cells29, 
			R.drawable.cells30, R.drawable.cells31, R.drawable.cells32};
	
	/* step = 0 -> alive cells step
	 * step = 1 -> content release step
	 */
	byte step;
	
	/* cellsMarking = 0 -> alive cells
	 * cellsMarking = 1 -> dead cells
	 * cellsMarking = 2 -> releasing cells
	 * cellsMarking = 3 -> remove cells one at a time
	 * cellsMarking = 4 -> not sure
	 */
	byte cellsMarking;
	
	private ArrayList<Marking> cellStatusList;
	private ArrayList<Marking> cellReleasingList;
	
	Bitmap aliveBitmap;
	Bitmap deadBitmap;
	Bitmap releasingBitmap;
	Bitmap notSureBitmap;
	
	Paint p;
	
	View markingView;
	
	LinearLayout layout;
	
	/*
	 * Class to store the icons with their coordinates
	 */
	public class Marking {
		Bitmap iconBitmap;
		float x;
		float y;
		
		/* typeOfMarking = 0 -> alive cells
		 * typeOfMarking = 1 -> dead cells
		 * typeOfMarking = 2 -> releasing cells
		 * typeOfMarking = 4 -> not sure
		 */
		byte typeOfMarking;
	}

	class DrawingView extends View {  
		
		private Canvas viewCanvas;
		
		public DrawingView(Context context) {  
			super(context);

			viewCanvas = new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true));
			this.setBackground(new BitmapDrawable(context.getResources(), bitmap));
		}
	
		@Override  
		public boolean dispatchTouchEvent(MotionEvent event) {  
			  
			// Check if the action has been to release the screen
			if (event.getAction() == MotionEvent.ACTION_UP) {  
				  
				// Create the new marking
				Marking newMarking = new Marking ();
				newMarking.x = event.getX();
				newMarking.y = event.getY();
	
				// Check the current marking
				if (cellsMarking == 0) {
					// Alive cells
					newMarking.iconBitmap = aliveBitmap;
					newMarking.typeOfMarking = cellsMarking;
					cellStatusList.add(newMarking);
					viewCanvas.drawBitmap(newMarking.iconBitmap, newMarking.x-25, newMarking.y-25, p);
				}
				else if (cellsMarking == 1) {
					// Dead cells
					newMarking.iconBitmap = deadBitmap;
					newMarking.typeOfMarking = cellsMarking;
					cellStatusList.add(newMarking);
					viewCanvas.drawBitmap(newMarking.iconBitmap, newMarking.x-25, newMarking.y-25, p);
				}
				else if (cellsMarking == 2) {
					// Releasing cells
					newMarking.iconBitmap = releasingBitmap;
					newMarking.typeOfMarking = cellsMarking;
					cellReleasingList.add(newMarking);
					viewCanvas.drawBitmap(newMarking.iconBitmap, newMarking.x-25, newMarking.y-25, p);
				}
				else if (cellsMarking == 4) {
					// Not sure cells
					newMarking.iconBitmap = notSureBitmap;
					newMarking.typeOfMarking = cellsMarking;
					if (step == 0)
						cellStatusList.add(newMarking);
					else cellReleasingList.add(newMarking); 
					viewCanvas.drawBitmap(newMarking.iconBitmap, newMarking.x-25, newMarking.y-25, p);
				}
				else {
					// Removal
					
					// Check the step in order to remove from the proper list
					// For removing, goes through the proper list descending
					// in order not to provoke an exception trying to access
					// a removed index; check if the distance between the touch
					// and every mark is less than 50, if so remove the mark
					if (step == 0){
						// List of alive cells
						for (int i=cellStatusList.size()-1; i>=0; i--){
							if (Math.sqrt(
									Math.pow((event.getX()-cellStatusList.get(i).x), 2)
									+Math.pow((event.getY()-cellStatusList.get(i).y), 2)) < 50){
								cellStatusList.remove(i);
							}
						}
					}
					else {
						// List of content releasing cells
						for (int i=cellReleasingList.size()-1; i>=0; i--){
							if (Math.sqrt(
									Math.pow((event.getX()-cellReleasingList.get(i).x), 2)
									+Math.pow((event.getY()-cellReleasingList.get(i).y), 2)) < 50){
								cellReleasingList.remove(i);
							}
						}
					}
				}
			}
			
			// Force redrawing of the canvas to display the new marking
		 	invalidate();  
		 	return true;  
		}  
		  
		@Override  
		protected void onDraw(Canvas canvas) {  
			super.onDraw(canvas);

			// When drawing, 25dp are substracted to the stored positions, since
			// the coordinates provided to the drawing method are for the top left
			// corner, instead of the center of the icon (that are the ones saved)
			
			// Check which step is being done
			if (step == 0) {
				// Step 0 is the one for marking cells as alive or not, so
				// check if there are already marked cells to be drawn
				if (cellStatusList.size() > 0) {

					// The list that stores the marked cells as alive is not
					// empty, so draws the icons
					for (int i=0; i<cellStatusList.size(); i++){
						if (cellStatusList.get(i).typeOfMarking == 0)
							canvas.drawBitmap(aliveBitmap, cellStatusList.get(i).x-25, cellStatusList.get(i).y-25, p);
						else if (cellStatusList.get(i).typeOfMarking == 1)
							canvas.drawBitmap(deadBitmap, cellStatusList.get(i).x-25, cellStatusList.get(i).y-25, p);
						else canvas.drawBitmap(notSureBitmap, cellStatusList.get(i).x-25, cellStatusList.get(i).y-25, p); 
					}
				}
			}
			else {
				// Step 1 is the one for marking cells that release content, so
				// check if there are already marked cells to be drawn
				if (cellReleasingList.size() > 0) {
					// The list that stores the cells releasing content is not
					// empty, so draws the icons
					for (int i=0; i<cellReleasingList.size(); i++){
						if (cellReleasingList.get(i).typeOfMarking == 2)
							canvas.drawBitmap(releasingBitmap, cellReleasingList.get(i).x-25, cellReleasingList.get(i).y-25, p);
						else canvas.drawBitmap(notSureBitmap, cellReleasingList.get(i).x-25, cellReleasingList.get(i).y-25, p);
					}
				}
			}
		}  
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_spotting_main);
        
        aliveBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alive_icon);
    	deadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dead_icon);
    	releasingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.release_icon);
    	notSureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notsure_icon);
    	
        p = new Paint();
        p.setColor(Color.RED);
        
        newTask();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cell_spotting_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == R.id.info_action) {
        	
        	View view = findViewById(R.id.info_action);
        	PopupMenu popupMenu = new PopupMenu (this, view);
        	popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){
        		
        		@Override
        		public boolean onMenuItemClick (MenuItem item){
        			
        			FragmentManager fm = getSupportFragmentManager();
        			CellSpottingDialog info_dialog;
        			
    				if (item.getItemId() == R.id.general_info){
    					// Click to open general info dialog
        				info_dialog = new CellSpottingDialog ((byte)0);
        			}
    				else if ((item.getItemId() == R.id.step_info) && (step == 0)){
    					// Click to open alive step info dialog
    					info_dialog = new CellSpottingDialog ((byte)1);
            		}
    				else {
    					// Click to open release step info dialog
        				info_dialog = new CellSpottingDialog ((byte)2);
            		}

    				// Show the dialog once it has been created according to the proper parameter
    				info_dialog.show(fm, "Dialog fragment");
    				
        			return true;
        		}
        	});
        	popupMenu.inflate(R.menu.info_menu);
        	popupMenu.show();
        	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /*
     * newTask
     * -------
     * Create, initialize everything for a new task
     * 
     * Pre: images not null
     * Post: cellsMarking = 0 
     * 		 and step = 0
     * 		 and new image loaded 
     * 		 and alive and dead buttons visible
     * 		 and release button hidden
     * 
     */
    public void newTask () {
    	
    	// Initialize the marking to alive cells
    	// cellsMarking = 0 -> alive cells marking
        cellsMarking = 0;
        
        // Create the arrays for saving the markings
        cellStatusList = new ArrayList<Marking>();
        cellReleasingList = new ArrayList<Marking>();

        // Get randomly the index of the image to display
		int imageId = (int)(Math.random()*images.length);
	    
		// Save the current image under analysis
		currentImage = imageId;
		
		// Round the selected image
		bitmap = BitmapFactory.decodeResource(getResources(), images[imageId]);
	        
		bitmap = getRoundedCornerBitmap(bitmap, 20);		
		
     	// Create the view which manages the marking
        markingView = new DrawingView (this);
        
        // Add the canvas for marking to the layout
        layout = (LinearLayout) findViewById(R.id.cellImageLayout);
        layout.removeAllViews();
        layout.addView(markingView, new LayoutParams(  
          LinearLayout.LayoutParams.MATCH_PARENT,  
          LinearLayout.LayoutParams.MATCH_PARENT));

    	// Initialize the step
        // step = 0 -> Alive cells step
        step = 0;
        
        // Set the proper step image in the progress bar
        progressBarView = (ImageView)findViewById(R.id.progressBarView);
        progressBarView.setBackgroundResource(R.drawable.steps_1_es);
        
        // Set initial focus
        Button alive_button = (Button) findViewById(R.id.alive_select_button);
        alive_button.setVisibility(View.VISIBLE);
		alive_button.setBackgroundResource(R.drawable.alive_hover_sp);
        
		Button dead_button = (Button) findViewById(R.id.dead_select_button);
        dead_button.setVisibility(View.VISIBLE);
		
        // Hide release button
        Button release_button = (Button) findViewById(R.id.release_select_button);
        release_button.setVisibility(View.GONE);
    }
    
    /*
     * getRoundedCornerBitmap
     * ----------------------
     * Round the corner of the image passed as a parameter
     * 	bitmap -> Image whose corners must be rounded
     * 	pixels -> Pixels rounded in the corners
     * 
     * Code from: http://stackoverflow.com/questions/14473113/bitmap-image-with-rounded-corners-with-stroke
     * 
     * Pre: pixels >= 0 and bitmap != null
     * Post: output != null
     * 
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    
    /*
     * selectAlive
     * -----------
     * Mark alive as the marking icon on the image
     * 	view -> View that launched the listener
     * 
     * Pre: view != null
     * Post: alive marking selected -> cellsMarking = 0
     * 
     */
    public void selectAlive (View view) {
    	cellsMarking = 0;
    	
    	// Unselect other buttons and select the proper one (graphically)
    	unselectButtons();
    	
    	Button alive_button = (Button) findViewById(R.id.alive_select_button);
        alive_button.setBackgroundResource(R.drawable.alive_hover_sp);
    }
    
    /*
     * selectDead
     * ----------
     * Mark dead as the marking icon on the image
     * 	view -> View that launched the listener
     * 
     * Pre: view != null
     * Post: dead marking selected -> cellsMarking = 1
     * 
     */
    public void selectDead (View view) {
    	cellsMarking = 1;
    	
		// Unselect other buttons and select the proper one (graphically)
    	unselectButtons();
    	
    	Button dead_button = (Button) findViewById(R.id.dead_select_button);
        dead_button.setBackgroundResource(R.drawable.dead_hover_sp);
    }
    
    /*
     * selectRelease
     * -------------
     * Mark release as the marking icon on the image
     * 	view -> View that launched the listener
     * 
     * Pre: view != null
     * Post: release marking selected -> cellsMarking = 2
     * 
     */
    public void selectRelease (View view) {
    	cellsMarking = 2;
    	
		// Unselect other buttons and select the proper one (graphically)
    	unselectButtons();
    	
    	Button release_button = (Button) findViewById(R.id.release_select_button);
        release_button.setBackgroundResource(R.drawable.release_hover_sp);
    }
    
    /*
     * selectNotSure
     * -------------
     * Mark not sure as the marking icon on the image
     * 	view -> View that launched the listener
     * 
     * Pre: view != null
     * Post: not sure as selected -> cellsMarking = 4
     * 
     */
    public void selectNotSure (View view) {
    	cellsMarking = 4;
    	
		// Unselect other buttons and select the proper one (graphically)
    	unselectButtons();
    	
    	Button not_sure_button = (Button) findViewById(R.id.not_sure_select_button);
        not_sure_button.setBackgroundResource(R.drawable.notsure_hover_sp);
    }
    
    /*
     * selectRemove
     * ------------
     * Select remove to remove cells one at a time
     * 	view -> View that launched the listener
     * 
     * Pre: view != null
     * Post: removal selected -> cellsMarking = 3
     * 
     */
    public void selectRemove (View view) {
    	cellsMarking = 3;
    	
		// Unselect other buttons and select the proper one (graphically)
    	unselectButtons();
    	
    	Button remove_button = (Button) findViewById(R.id.remove_select_button);
        remove_button.setBackgroundResource(R.drawable.remove_hover_sp);
    }
    
    /*
     * selectRemoveAll
     * ---------------
     * Empty the proper cells array and remove graphically from the canvas
     * 	view -> View that launched the listener
     * 
     * Pre: view != null
	 *		and (step = 0 or step = 1)
     * Post: if (step=0) -> cellStatusList empty
     * 		 else if (step=1) -> cellReleasingList empty
     * 		 and canvas is redrawn
     * 
     */
    public void selectRemoveAll (View view) {

    	// Check the step in order to clear the proper array
    	if (step == 0)
    		cellStatusList.clear();
    	else cellReleasingList.clear();
    	
    	// Force redraw, so marks are removed
    	markingView.invalidate();
    }
    
    /*
     * nextStep
     * --------
     * Set the necessary to move to the next step or to complete the task
     * 	view -> View that launched the listener
     * 
     * Pre: view != null
	 *		and (step = 0 or step = 1)
     * Post: 
     * 
     */
    public void nextStep (View view) {
    	
    	// Check the current step
    	if (step == 0){
    		
    		// Check that cellStatusList is not empty
    		if (cellStatusList.size() > 0){
    			// Move to the next step
    			// step = 1 -> Cells releasing content step
    			step = 1;
        
    			// Set the proper step image in the progress bar
    			progressBarView = (ImageView)findViewById(R.id.progressBarView);
    			progressBarView.setBackgroundResource(R.drawable.steps_2_es);
        
    			// Set release button visible and give it to it the initial focus
    			Button release_button = (Button) findViewById(R.id.release_select_button);
    			release_button.setVisibility(View.VISIBLE);
    			cellsMarking = 2;
    			unselectButtons();
    			release_button.setBackgroundResource(R.drawable.release_hover_sp);
        
    			// Hide alive and dead buttons
    			Button alive_button = (Button) findViewById(R.id.alive_select_button);
    			alive_button.setVisibility(View.GONE);
    			Button dead_button = (Button) findViewById(R.id.dead_select_button);
    			dead_button.setVisibility(View.GONE);
        
    			// Force redraw, so marks are removed
    			markingView.invalidate();
    		}
    		else {
    			// cellStatusList is empty
    			
    			// Instantiate the alert dialog builder
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			
    			// Set the values of the dialog
    			builder.setMessage(R.string.no_marks_dialog).setTitle(R.string.no_marks_dialog_title);
    			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						// Do nothing
					}
				});
    			
    			// Create the alert dialog
    			builder.create();
    			builder.show();
    		}
    	}
    	else {
    		// step = 1
    		
    		// Check if number of releasing cells is not greater than number of cells from previous step
    		if (cellReleasingList.size()>cellStatusList.size()) {
    			// cellStatusList is empty
    			
    			// Instantiate the alert dialog builder
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			
    			// Set the values of the dialog
    			builder.setMessage(R.string.more_cells_than_status_toast).setTitle(R.string.more_cells_than_status_dialog_title);
    			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						// Do nothing
					}
				});
    			
    			// Create the alert dialog
    			builder.create();
    			builder.show();
    		}
    		else {
    			// Move to a new task
    			newTask();
    		
    			// Show a toast
    			int duration = Toast.LENGTH_SHORT;
    			Toast toast = Toast.makeText(this, R.string.completed_toast, duration);
    			toast.show();
    		}
    	}
    }
    
    /*
     * unselectButtons
     * ---------------
     * Set the default icon to all buttons
     * 
     * Pre: 
     * Post: All button backgrounds set to the default one
     * 
     */
    public void unselectButtons () {
    	Button alive_button = (Button) findViewById(R.id.alive_select_button);
        alive_button.setBackgroundResource(R.drawable.alive_sp);
        Button dead_button = (Button) findViewById(R.id.dead_select_button);
        dead_button.setBackgroundResource(R.drawable.dead_sp);
        Button release_button = (Button) findViewById(R.id.release_select_button);
        release_button.setBackgroundResource(R.drawable.release_sp);
        Button not_sure_button = (Button) findViewById(R.id.not_sure_select_button);
        not_sure_button.setBackgroundResource(R.drawable.notsure_sp);
        Button remove_button = (Button) findViewById(R.id.remove_select_button);
        remove_button.setBackgroundResource(R.drawable.remove_sp);
    }
   
}
