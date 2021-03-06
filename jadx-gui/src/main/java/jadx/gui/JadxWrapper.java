package jadx.gui;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.JadxDecompiler;
import jadx.api.JavaNode;
import jadx.api.JavaClass;
import jadx.api.JavaPackage;
import jadx.api.ResourceFile;
import jadx.gui.settings.JadxSettings;

public class JadxWrapper {
	private static final Logger LOG = LoggerFactory.getLogger(JadxWrapper.class);
	private static JadxWrapper curWrapper = null;

	private final JadxSettings settings;
	private JadxDecompiler decompiler;
	private File openFile;
	private List<ResourceFile> selectedResources;
	private List<JavaClass> selectedClasses;

	public JadxWrapper(JadxSettings settings) {
		this.settings = settings;
		selectedResources = new ArrayList<ResourceFile>();
		selectedClasses = new ArrayList<JavaClass>();
		
		curWrapper = this;
	}

	public void openFile(File file) {
		this.openFile = file;

		clearSelect();

		try {
			this.decompiler = new JadxDecompiler(settings.toJadxArgs());
			this.decompiler.getArgs().setInputFiles(Collections.singletonList(file));
			this.decompiler.load();
		} catch (Exception e) {
			LOG.error("Error load file: {}", file, e);
		}
	}

	public void clearSelect() {
		selectedResources.clear();
		selectedClasses.clear();
	}

	public void addSelectedResNode(ResourceFile res) {
		selectedResources.add(res);
	}

	public void addSelectedClassNode(JavaClass cls) {
		selectedClasses.add(cls);
	}

	/* public void saveSelect(final File dir, final ProgressMonitor progressMonitor) {
		Runnable save = new Runnable() {
			@Override
			public void run() {
					try {
						decompiler.getArgs().setRootDir(dir);
						ThreadPoolExecutor ex = (ThreadPoolExecutor) decompiler.getSaveExecutor(selectedResources, selectedClasses);
						ex.shutdown();
						while (ex.isTerminating()) {
							long total = ex.getTaskCount();
							long done = ex.getCompletedTaskCount();
							progressMonitor.setProgress((int) (done * 100.0 / (double) total));
							Thread.sleep(500);
						}
						progressMonitor.close();
						LOG.info("done");
					} catch (InterruptedException e) {
						LOG.error("Save interrupted", e);
						Thread.currentThread().interrupt();	
			         }
		      }
        };
		new Thread(save).start();
	} */

	public void saveAll(final File dir, final ProgressMonitor progressMonitor) {
		Runnable save = new Runnable() {
			@Override
			public void run() {
				try {
					decompiler.getArgs().setRootDir(dir);
					ThreadPoolExecutor ex = (ThreadPoolExecutor) decompiler.getSaveExecutor(/* null, null */);
					ex.shutdown();
					while (ex.isTerminating()) {
						long total = ex.getTaskCount();
						long done = ex.getCompletedTaskCount();
						progressMonitor.setProgress((int) (done * 100.0 / (double) total));
						Thread.sleep(500);
					}
					progressMonitor.close();
					LOG.info("decompilation complete, freeing memory ...");
					decompiler.getClasses().forEach(JavaClass::unload);
					LOG.info("done");
				} catch (InterruptedException e) {
					LOG.error("Save interrupted", e);
					Thread.currentThread().interrupt();
				}
			}
		};
		new Thread(save).start();
	}
	
	public static List<JavaClass> getSelectedClasses() 
	{
		return curWrapper.selectedClasses;
	}
	
	public static List<ResourceFile> getSelectedResources()
	{
		return curWrapper.selectedResources;
	}

	public List<JavaClass> getClasses() {
		return decompiler.getClasses();
	}

	public List<JavaPackage> getPackages() {
		return decompiler.getPackages();
	}

	public List<ResourceFile> getResources() {
		return decompiler.getResources();
	}

	public File getOpenFile() {
		return openFile;
	}
}
