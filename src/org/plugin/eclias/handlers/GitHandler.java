package org.plugin.eclias.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.plugin.eclias.downloadGitCommits.MainDownloadGitCommits;
import org.plugin.eclias.goldSetsGeneratorFromSVNCommits.MainGoldSetsGeneratorFromSVNCommits;
import org.plugin.eclias.views.EcliasView;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GitHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public GitHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information from
	 * the application context.
	 */
	public static StyledText repoAddress;
	public static StyledText startRevision;
	public static StyledText endRevision;
	public static String repoAddressText;
	public static String startRevisionText;
	public static String endRevisionText;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		boolean[] result = new boolean[1];
		Shell shell = EcliasView.viewer.getControl().getShell();
		Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout(2, false));

		GridData queryGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		queryGridData.widthHint = 300;

		Label resultsLabel = new Label(dialog, SWT.PUSH);
		resultsLabel.setText("Repository Address:");
		resultsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		repoAddress = new StyledText(dialog, SWT.SINGLE | SWT.BORDER);
		repoAddress.setLayoutData(queryGridData);
		repoAddress.setText("https://github.com/vasanthgeethanraju/SVNKit.git");

		Label resultsLabel1 = new Label(dialog, SWT.PUSH);
		resultsLabel1.setText("Start Commit");
		resultsLabel1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		startRevision = new StyledText(dialog, SWT.SINGLE | SWT.BORDER);
		startRevision.setLayoutData(queryGridData);

		Label resultsLabel2 = new Label(dialog, SWT.PUSH);
		resultsLabel2.setText("End Commit");
		resultsLabel2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		endRevision = new StyledText(dialog, SWT.SINGLE | SWT.BORDER);
		endRevision.setLayoutData(queryGridData);

		Button ok = new Button(dialog, SWT.PUSH);
		ok.setText("OK");
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				result[0] = event.widget == ok;
				try {
					System.out.println("it is coming and checking maindownload svncommit");
					System.out.println(endRevision.getText() + " :end rev");
					repoAddressText = repoAddress.getText();
					startRevisionText = startRevision.getText();
					endRevisionText = endRevision.getText();
					MainDownloadGitCommits.testDownloadGitCommits(repoAddressText, startRevisionText, endRevisionText);
//					MainGoldSetsGeneratorFromSVNCommits.testArgoUML();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.close();
				MessageDialog.openInformation(shell, "GIT Options",
						"The data has been saved in the folder:" + MainDownloadGitCommits.outputFolder);
			}
		};
		ok.addListener(SWT.Selection, listener);
		Button cancel = new Button(dialog, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				dialog.close();
			}
		});
		dialog.setText("GIT Options");
		dialog.pack();
		dialog.open();

		return null;
	}

}
