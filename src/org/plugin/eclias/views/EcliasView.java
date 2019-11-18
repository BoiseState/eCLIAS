package org.plugin.eclias.views;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.*;
import org.plugin.eclias.corpus.MainCorpusGenerator;
import org.plugin.eclias.index.LuceneWriteIndexFromFile;
import org.plugin.eclias.preprocessor.MainCorpusPreprocessor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class EcliasView extends ViewPart {
	
//	private static final String NO_FILTER_STRING = "[no filter]";

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.plugin.eclias.views.EcliasView";

	@Inject IWorkbench workbench;
	
	private TableViewer viewer;

	private Composite queryComposite;
	private Label queryLabel;
	private Button searchButton;
	private Button clearButton;
	private StyledText queryText;
	private StyledText queryText1;


	//private MethodListWidget resultList;
	private Label resultsLabel;
	private MethodListWidget resultList;

	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	
//	class NameSorter extends ViewerSorter {
//	}

	/**
	 * The constructor.
	 */
	public EcliasView() {

	}

	
	@Override
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(new String[] { "eCLIAS Tool" });
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "org.plugin.eclias.viewer");
		getSite().setSelectionProvider(viewer);
//		makeActions();
//		hookContextMenu();
//		hookDoubleClickAction();
//		contributeToActionBars();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);

		queryComposite = new Composite(parent, SWT.PUSH);
		GridData queryCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true,
				false);
		queryComposite.setLayoutData(queryCompositeGridData);
		GridLayout queryCompositeLayout = new GridLayout();
		queryCompositeLayout.numColumns = 3;
		queryCompositeLayout.marginWidth = 0;
		queryComposite.setLayout(queryCompositeLayout);
		
		queryLabel = new Label(queryComposite, SWT.PUSH);
		queryLabel.setText("Search Text:");
		GridData queryLabelGridData = new GridData(SWT.LEFT, SWT.BOTTOM, true,
				false);
		queryLabel.setLayoutData(queryLabelGridData);
		searchButton = new Button(queryComposite, SWT.PUSH);
		searchButton.setText("Search");
		GridData searchButtonGridData = new GridData(SWT.END, SWT.END, false,
				false);
		searchButton.setLayoutData(searchButtonGridData);
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				startSearch();
				try {
					String s = LuceneWriteIndexFromFile.search(queryText.getText());
//					MainCorpusGenerator mcg = new MainCorpusGenerator();
//					mcg.main(null);
//					System.out.println("Corpus Generated");
//					MainCorpusPreprocessor mcp = new MainCorpusPreprocessor();
//					mcp.main(null);
//					System.out.println("Preprocessing Generated");
					
//					String names = s.replaceAll(",", "");
					queryText1.setText(s);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		clearButton = new Button(queryComposite, SWT.PUSH);
		clearButton.setText("Clear");
		GridData clearButtonGridData = new GridData(SWT.END, SWT.END, false,
				false);
		clearButton.setLayoutData(clearButtonGridData);
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				queryText.setText("");
//				resultsLabel.setText("");
				queryText1.setText("");
			}
		});

		queryText = new StyledText(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);
		queryText.setEditable(true);
		GridData queryGridData = new GridData(SWT.FILL, SWT.BEGINNING, true,
				false);
		queryGridData.heightHint = 50;
		queryText.setLayoutData(queryGridData);
		queryText.setAlwaysShowScrollBars(false);
		queryText.addVerifyKeyListener(new VerifyKeyListener() {

			public void verifyKey(VerifyEvent event) {
				if (event.keyCode == SWT.KEYPAD_CR || event.keyCode == SWT.CR) {
					event.doit = false;
					try {
						LuceneWriteIndexFromFile.search(queryText.getText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					startSearch();
				}
			}
		});

		queryText.setText("");
		
		resultsLabel = new Label(parent, SWT.PUSH);
		resultsLabel.setText("Results:");
		resultsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
		queryText1 = new StyledText(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);
		queryText1.setEditable(false);
		GridData queryGridData1 = new GridData(SWT.FILL, SWT.BEGINNING, true,
				false);
		queryGridData1.heightHint = 100;
		queryText1.setLayoutData(queryGridData1);
		queryText1.setAlwaysShowScrollBars(false);
//		queryText1.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//					
//			}
//			});
		
			 
//		resultList = new MethodListWidget(parent, SWT.H_SCROLL | SWT.V_SCROLL
//				| SWT.BORDER, true);
	
	}


	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Eclias Tool",
			message);
	}
	
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
		

}
