package org.plugin.eclias.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.*;
import org.plugin.eclias.index.LuceneWriteIndexFromFile;
import org.plugin.eclias.index.LuceneWriteIndexFromFile.Score;
import org.plugin.eclias.index.StartMSRAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.plugin.eclias.views.RevealInEditorAction;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class EcliasView extends ViewPart {

//	private static final String NO_FILTER_STRING = "[no filter]";

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.plugin.eclias.views.EcliasView";

	@Inject
	IWorkbench workbench;

	public static TableViewer viewer;
	public static Boolean useStopWords = false;
	public static Boolean usePorterStemmer = false;
	public static Boolean useDigits = false;
	public static Boolean useOriginal = false;
	public static Boolean useSplitIdentifiers = false;

	private Composite queryComposite;
	private Label queryLabel;
	private Button optionsButton;
	private Button searchButton;
	private Button clearButton;
	private StyledText queryText;
	private Table table;

	private Label resultsLabel;
	private TableItem item;

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

		IActionBars lBars = getViewSite().getActionBars();
		fillLocalToolBar(lBars.getToolBarManager());
//		fillToolBarMenu(lBars.getMenuManager());

		queryComposite = new Composite(parent, SWT.PUSH);
		GridData queryCompositeGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		queryComposite.setLayoutData(queryCompositeGridData);
		GridLayout queryCompositeLayout = new GridLayout();
		queryCompositeLayout.numColumns = 4;
		queryCompositeLayout.marginWidth = 0;
		queryComposite.setLayout(queryCompositeLayout);

		queryLabel = new Label(queryComposite, SWT.PUSH);
		queryLabel.setText("Search Text:");
		GridData queryLabelGridData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
		queryLabel.setLayoutData(queryLabelGridData);

		optionsButton = new Button(queryComposite, SWT.PUSH);
		optionsButton.setText("Choose Indexing Options");
		GridData optionsButtonGridData = new GridData(SWT.RIGHT, SWT.END, false, false);
		optionsButton.setLayoutData(optionsButtonGridData);
		optionsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = viewer.getControl().getShell();
				Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialog.setLayout(new RowLayout(3));

				Button checkBox3 = new Button(dialog, SWT.CHECK);
				checkBox3.setText("Keep Digits");
				checkBox3.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						Button btn = (Button) event.getSource();
						useDigits = btn.getSelection();
						System.out.println("Keep Digits:" + useDigits);
					}
				});

				Button checkBox = new Button(dialog, SWT.CHECK);
				checkBox.setText("Split Identifiers");
				checkBox.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						Button btn = (Button) event.getSource();
						useSplitIdentifiers = btn.getSelection();
						System.out.println("Use Split Identifiers:" + useSplitIdentifiers);
					}
				});

				Button checkBox4 = new Button(dialog, SWT.CHECK);
				checkBox4.setText("Keep the Original (Compound) identifiers");
				checkBox4.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						Button btn = (Button) event.getSource();
						useOriginal = btn.getSelection();
						System.out.println("Original identifiers:" + useOriginal);
					}
				});

				Button checkBox1 = new Button(dialog, SWT.CHECK);
				checkBox1.setText("Use Stop Words");
				checkBox1.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						Button btn = (Button) event.getSource();
						useStopWords = btn.getSelection();
						System.out.println("Use Stop Words:" + useStopWords);
					}
				});
				Button checkBox2 = new Button(dialog, SWT.CHECK);
				checkBox2.setText("Use Porter Stemmer");
				checkBox2.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						Button btn = (Button) event.getSource();
						usePorterStemmer = btn.getSelection();
						System.out.println("Use porter stemmer:" + usePorterStemmer);
					}
				});

				Button ok = new Button(dialog, SWT.PUSH);
				ok.setText("OK");
				ok.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent event) {
						dialog.close();
					}
				});
				checkBox.setSelection(useSplitIdentifiers);
				checkBox4.setSelection(useOriginal);
				checkBox1.setSelection(useStopWords);
				checkBox2.setSelection(usePorterStemmer);
				checkBox3.setSelection(useDigits);

				dialog.setText("Choose Indexing Options");
				dialog.pack();
				dialog.open();

				System.out.println("options button is clicked");
			}
		});

		searchButton = new Button(queryComposite, SWT.PUSH);
		searchButton.setText("Search");
		GridData searchButtonGridData = new GridData(SWT.END, SWT.END, false, false);
		searchButton.setLayoutData(searchButtonGridData);
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				startSearch();

				try {
					LuceneWriteIndexFromFile.index();
					ArrayList<Score> s = LuceneWriteIndexFromFile.search(queryText.getText());

//					MainCorpusGenerator mcg = new MainCorpusGenerator();
//					mcg.main(null);
//					System.out.println("Corpus Generated");
//					MainCorpusPreprocessor mcp = new MainCorpusPreprocessor();
//					mcp.main(null);
//					System.out.println("Preprocessing Generated");
//					MessageDialog.openInformation(shell, "Eclias", "Corpus Extracted and Indexed for the following projects:");

					for (Score sc : s) {
						resultsLabel.setText(sc.getHits());
						item = new TableItem(table, SWT.NONE);
						item.setText(0, sc.getClassName() + "");
						item.setText(1, sc.getMethodName() + "");
						item.setText(2, sc.getScore() + "");
						item.setText(3, sc.getPackageName() + "");
					}
					table.addSelectionListener(new SelectionAdapter() {

						public void widgetSelected(SelectionEvent e) {

							String classname = ((TableItem) e.item).getText(0);
							String methodname = ((TableItem) e.item).getText(1);
							setFocus();

							try {
								ArrayList<Score> s = LuceneWriteIndexFromFile.search(queryText.getText());
								for (Score sc : s) {

									if (classname.equals(sc.getClassName()) && methodname.equals(sc.getMethodName())) {
										(new RevealInEditorAction(sc.getMethod())).run();
									} else {
										continue;
									}
									System.out.println("its not breaking");

								}

							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}

					});

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		clearButton = new Button(queryComposite, SWT.PUSH);
		clearButton.setText("Clear");
		GridData clearButtonGridData = new GridData(SWT.END, SWT.END, false, false);
		clearButton.setLayoutData(clearButtonGridData);
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resultsLabel.setText("Results:");
				queryText.setText("");
				table.removeAll();
				useStopWords = false;
				usePorterStemmer = false;
				useDigits = false;
				useOriginal = false;
				useSplitIdentifiers = false;
			}
		});

		queryText = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		queryText.setEditable(true);
		GridData queryGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		queryGridData.heightHint = 50;
		queryText.setLayoutData(queryGridData);
		queryText.setAlwaysShowScrollBars(false);

		queryText.setText("");

		resultsLabel = new Label(parent, SWT.PUSH);
		resultsLabel.setText("Results:");
		resultsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData queryGridData1 = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		queryGridData1.heightHint = 100;
		table.setLayoutData(queryGridData1);

		String[] titles = { "Class Name", "Method Name", "Score", "Package Name" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}

		table.setSize(table.computeSize(SWT.DEFAULT, 200));

	}

//	private void showMessage(String message) {
//		MessageDialog.openInformation(viewer.getControl().getShell(), "Eclias Tool", message);
//	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void fillLocalToolBar(IToolBarManager pManager) {

		pManager.add(new StartMSRAction());

		// TODO: ADD MSR button right here
	}

}
