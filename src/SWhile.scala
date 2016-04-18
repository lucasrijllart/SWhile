import java.awt
import java.awt.datatransfer.StringSelection
import java.awt.{Color, Dimension, GridLayout}
import java.io.{BufferedWriter, File, FileWriter}
import javax.swing._
import javax.swing.event.{UndoableEditEvent, UndoableEditListener, DocumentEvent, DocumentListener}
import javax.swing.filechooser.FileFilter
import javax.swing.text._
import javax.swing.undo.{CannotUndoException, UndoManager}

import datatoprogram.DataToProgram
import src.interpreter.{InterpreterException, ProgramParser}

import scala.collection.mutable.ListBuffer
import scala.swing.event._
import scala.swing.{Action, _}

object SWhile extends SimpleSwingApplication {

  //put recent files here
  var activeFileList = ListBuffer[WFile]()
  var activeFile = new WFile("")
  var lines = 1

  def top = new MainFrame {
    iconImage = toolkit.getImage("/src/logo.png")
    getClass.getResource("logo.png")
    title = "SWhile"
    visible = true
    preferredSize = new Dimension(800, 700)

    //Create and get menu bar
    menuBar = getMenuBar



    //buttons
    val runButton = new Button {
      text = "Run"
    }

    /*
    //line numbers panel
    val lineNumbersPane = new GridPanel(1, 1) {
      contents += lineNumbers
      preferredSize = new Dimension(30, 1000)
      minimumSize = new Dimension(30, 1000)
      maximumSize = new Dimension(30, 1000)
      background = Color.lightGray
    }*/

    /*
    val lineNumbers2 = new Label() {
      font = new Font("Monaco", 0, 14)
      text = "1"
      background = Color.lightGray
      preferredSize = new Dimension(30, 100)
    }
    val lineNumbersPane2 = new ScrollPane(lineNumbers2) {
      preferredSize = new Dimension(30, 100)
    }
    */

    /*
    //code editor
    val codeTextArea = new TextArea {
      font = new Font("Monaco", 0, 14)
      tabSize = 2
      lineWrap = false
      enabled = false
    }
    */

    val doc:StyledDocument = new DefaultStyledDocument() {

    }
    val codeTextArea = new JTextPane(doc) {
      setFont(new swing.Font("Courier", 0, 14))
      setEnabled(false)
      addStyle("Normal", null)
      addStyle("Keyword", getStyle("Normal"))
      addStyle("Command", getStyle("Normal"))
      addStyle("NumBool", getStyle("Normal"))
      addStyle("List", getStyle("Normal"))
      addStyle("BreakLine", getStyle("Normal"))
      StyleConstants.setBold(getStyle("Keyword"), true)
      StyleConstants.setForeground(getStyle("Command"), new Color(0, 0, 204))
      StyleConstants.setForeground(getStyle("NumBool"), new Color(51, 102, 153))
      StyleConstants.setForeground(getStyle("List"), new Color(187, 95, 11))
      StyleConstants.setBackground(getStyle("BreakLine"), new Color(208, 35, 37))

      val sc: StyleContext = StyleContext.getDefaultStyleContext
      var tabs: Array[TabStop] = new Array[TabStop](30)
      var iterator = 31
      for (a <- 0 to 29) {
        tabs(a) = new TabStop(iterator, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE)
        iterator += 31
      }


      val tabset: TabSet = new TabSet(tabs)
      val paraSet: AttributeSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabset)
      doc.setParagraphAttributes(0, 0, paraSet, false)

      val lis = new DocumentListener {
        override def insertUpdate(e: DocumentEvent): Unit = {
          printLines("")
          syntaxHighlightLines(e.getOffset)
        }


        override def changedUpdate(e: DocumentEvent): Unit = {}

        override def removeUpdate(e: DocumentEvent): Unit = {
          printLines("")
          syntaxHighlightLines(e.getOffset)
        }
      }
      doc.addDocumentListener(lis)

     def syntaxHighlightLines(eventOffset: Int) = {
       val highlight = new Runnable {
         override def run(): Unit = {
           val textArray = getText.split("\n")
           val changedLineNumber = getDocument.getDefaultRootElement.getElementIndex(eventOffset)
           if (changedLineNumber < textArray.length) {
             val currentChangedLine = textArray(changedLineNumber).replace(";", " ; ").replace("[", " [ ").replace("]", " ] ").replace(",", " , ")
             //get the words separated by spaces
             val words = currentChangedLine.split(" +|\t|\n")
             //for every word, check if it needs highlighting
             for (word <- words) {
               if (word.matches("read|write|cons|hd|tl")) {
                 doc.setCharacterAttributes(getText.lastIndexOf(word), word.length, getStyle("Keyword"), true)
               } else if (word.matches(":=|while|if|else|switch|case")) {
                 doc.setCharacterAttributes(getText.lastIndexOf(word), word.length, getStyle("Command"), true)
               } else if (word.matches("nil|true|false|[0-9]+")) {
                 doc.setCharacterAttributes(getText.lastIndexOf(word), word.length, getStyle("NumBool"), true)
               } else if (word.matches("\\[|\\]|,")) {
                 doc.setCharacterAttributes(getText.lastIndexOf(word), word.length, getStyle("List"), true)
               } else {
                 doc.setCharacterAttributes(getText.lastIndexOf(word), word.length, getStyle("Normal"), true)
               }
             }
           }
         }
       }
       SwingUtilities.invokeLater(highlight)
      }

      def syntaxHighlightAll = {
        var indexIterator = 0
        val highlight = new Runnable {
          override def run(): Unit = {
            val text = getText.replace(";", " ; ").replace("[", " [ ").replace("]", " ] ").replace(",", " , ")
            val words = text.split(" +|\n|\t")

            for (word <- words) {
              if (word.matches("read|write|cons|hd|tl")) {
                doc.setCharacterAttributes(getText.indexOf(word, indexIterator), word.length, getStyle("Keyword"), true)
                indexIterator = getText.indexOf(word, indexIterator)
              } else if (word.matches(":=|while|if|else|switch|case")) {
                doc.setCharacterAttributes(getText.indexOf(word, indexIterator), word.length, getStyle("Command"), true)
                indexIterator = getText.indexOf(word, indexIterator)
              } else if (word.matches("nil|true|false|[0-9]+")) {
                doc.setCharacterAttributes(getText.indexOf(word, indexIterator), word.length, getStyle("NumBool"), true)
                indexIterator = getText.indexOf(word, indexIterator)
              } else if (word.matches("\\[|\\[\\]|,")) {
                doc.setCharacterAttributes(getText.indexOf(word, indexIterator), word.length, getStyle("List"), true)
                indexIterator = getText.indexOf(word, indexIterator)
              } else {
                doc.setCharacterAttributes(getText.indexOf(word, indexIterator), word.length, getStyle("Normal"), true)
                indexIterator = getText.indexOf(word, indexIterator)
              }
            }
          }
        }
        SwingUtilities.invokeLater(highlight)
      }
    }

    val undo = new UndoManager()
    codeTextArea.getDocument.addUndoableEditListener(new UndoableEditListener {
      def undoableEditHappened(e: UndoableEditEvent) = {
        undo.addEdit(e.getEdit)
      }
    })

    codeTextArea.getActionMap.put("Undo",
      new AbstractAction("Undo") {
        override def actionPerformed(e: awt.event.ActionEvent): Unit = {
          try {
            if (undo.canUndo) {
              undo.undo()
            }
          } catch {
            case e:CannotUndoException =>
          }
        }
      })

    //codeTextArea.getInputMap.put(KeyStroke.getKeyStroke("control Z"), "Undo")

    codeTextArea.getActionMap.put("Redo",
      new AbstractAction("Redo") {
        override def actionPerformed(e: awt.event.ActionEvent): Unit = {
          try {
            if (undo.canRedo) {
              undo.redo()
            }
          } catch {
            case e:CannotUndoException =>
          }
        }
      })
    //codeTextArea.getInputMap.put(KeyStroke.getKeyStroke("control Y"), "Redo")

    /*
    val undoManager = new UndoManager
    codeTextArea.getDocument.addUndoableEditListener(undoManager)

    val undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.VK_META)
    val redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.VK_META)

    val undoAction = new javax.swing.Action(undoManager)
    codeTextArea.getInputMap.put(undoKeystroke, "undoKeystroke")
    codeTextArea.getActionMap.put("undoKeystroke", undoAction)



    val redoAction = new RedoAction(undoManager)
    codeTextArea.getInputMap.put(redoKeystroke, "redoKeystroke")
    codeTextArea.getActionMap.put("redoKeystroke", redoAction)
  */

    val scrollCodeTextArea = new JScrollPane(codeTextArea)


    val codeTextAreaComponent = Component.wrap(codeTextArea)

    //line numbers textArea
    val lineNumbers = new JTextArea {
      setFont(new Font("Courier", 0, 14))
      setText("")
      setBackground(new Color(220, 220, 220))
      setEditable(false)
      setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.lightGray))
    }
    def printLines(s: String): Unit = {
      val current = codeTextArea.getDocument.getDefaultRootElement.getElementCount
      //println("lines:" + lines + " | current:" + current)
      if (current == lines - 1) {
        //println("reduce")
        lines -= 1
        //println("Linenumbers.text.length:" + lineNumbers.text.length)
        //println("Lines.length:" + (lines.toString.length+1))
        lineNumbers.setText(lineNumbers.getText.substring(0,lineNumbers.getText.length - (lines.toString.length + 1) ))//lines.toChar.length
      } else if (current == lines + 1) {
        //println("add")
        lines += 1
        lineNumbers.setText(lineNumbers.getText.concat("\n" + lines.toString))
      } else if (current == lines) {
      } else {
        //println("else")
        codeTextArea.syntaxHighlightAll
        lines = current
        lineNumbers.setText("")
        for (i <- 1 until current) {
          lineNumbers.setText(lineNumbers.getText() + i + "\n")
        }
        lineNumbers.setText(lineNumbers.getText + current.toString)
      }
    }
    val lineNumbersPane = new JPanel() {
      setLayout(new GridLayout(1, 1))
      add(lineNumbers)
      setPreferredSize(new Dimension(30, codeTextArea.getHeight))
    }

    val p = new BoxPanel(Orientation.Horizontal) {
      //contents = codeTextArea2
    }

    val listBuffer = ListBuffer[String]()

    //make navigator gutter
    val listView = new ListView[String]() {
      listData = listBuffer
      //listData = listBuffer //put listModel here
      preferredSize = new Dimension(140, 200)
      //selectionBackground = new swing.Color(36, 116, 218)
      //selectionForeground = new Color(255, 255, 255)
      background = new swing.Color(220, 224, 226) //new Color(39, 49, 57)
      //foreground = new Color(203, 213, 221)
      listenTo(mouse.clicks, this.selection)
      reactions += {
        case e: MouseClicked =>
          //display selected file on screen
          //println(SwingUtilities.isRightMouseButton(e.peer))
          //println(e.source.name)
          //println(e.peer)
        case SelectionChanged(listView: Component) =>
          //println(this.selection.items)
          val selected = this.selection.items.headOption //get selected file name
          //println("- Selected:" + selected)
          if (selected.isDefined) {
            //println("Active files:" + activeFileList)
            for (file <- activeFileList) {
              //for each file in activeFileList
              //println("Looking at:" + file.getName)
              if (file.getName.equals(selected.get)) {
                //println("Selected " + file.getName)
                //save text into active file before changing to other file
                activeFile.setContents(codeTextArea.getText)
                //set area to contents
                //println(file.getName + " contents:\n" + file.getContents)
                codeTextArea.setText(file.getContents)
                activeFile = file
              }
            }
          }
      }
      maximumSize = new Dimension(150, 500)
    }

    val scrollListView = new ScrollPane(listView) {
      minimumSize = new Dimension(100, 500) //sets how small the panel can be
      maximumSize = new Dimension(200, 500)
    }

    val codePanel = new JPanel() {
      add(lineNumbersPane)
      add(codeTextArea)
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS))
    }

    val scrollCodePanel = new JScrollPane(codePanel)

    val inputArea = new TextArea() {
      rows = 4
      preferredSize = new Dimension(400, 40)
      lineWrap = true
    }
    val inputPanel = new BoxPanel(Orientation.Vertical) {
      //contents += new Label("Input:")
      contents += new ScrollPane(inputArea)
      contents += runButton
      border = BorderFactory.createTitledBorder("Input")
      background = new Color(220, 224, 226)
    }

    val outputTextField = new TextArea() {
      rows = 6
      preferredSize = new Dimension(400, 40)
      lineWrap = true
    }
    val outputPanel = new BoxPanel(Orientation.Vertical) {
      //contents += new Label("Output:")
      outputTextField.editable = false
      contents += new ScrollPane(outputTextField)
      //border = BorderFactory.createEtchedBorder()
      border = BorderFactory.createTitledBorder("Output")
      background = new Color(220, 224, 226)
    }

    val console = new TextArea() {
      rows = 10
      background = new swing.Color(220, 220, 220)
      font = new Font("Monaco", 0, 12)
      editable = false
      preferredSize = new Dimension(150, 300)
    }
    val consolePanel = new BoxPanel(Orientation.Vertical) {
      contents += new ScrollPane(console)
      border = BorderFactory.createTitledBorder("Console")
      background = new Color(220, 224, 226)
    }

    val IOPanel = new SplitPane(Orientation.Horizontal, inputPanel, outputPanel) {
      continuousLayout = true
      dividerSize = 1
    }

    val IOCPanel = new GridPanel(2, 1) {
      contents += IOPanel
      contents += consolePanel
      minimumSize = new Dimension(200, 300) //sets how small the panel can be
    }

    val codePanelScrollPane = Component.wrap(scrollCodePanel) //DOESNT SCROLL
    val codeAndIOCPanel = new SplitPane(Orientation.Vertical, codePanelScrollPane, IOCPanel) {
      oneTouchExpandable = true
      dividerLocation = 475
      continuousLayout = true
      //dividerSize = 12 //make divider bigger
      border = BorderFactory.createEmptyBorder()
    }


    //panel with navigator next to code
    val mainFrame = new SplitPane(Orientation.Vertical, scrollListView, codeAndIOCPanel) {
      oneTouchExpandable = true
      continuousLayout = true
      border = BorderFactory.createEmptyBorder()
      dividerLocation = 100 //sets where the division is (100 is the min for listview)
      //dividerSize = 1
    }

    //panel with buttons
    val editor = new BoxPanel(Orientation.Vertical) {
      contents += mainFrame
    }

    contents = mainFrame

    listenTo(codeTextAreaComponent, runButton)
    listenTo()

    reactions += {
      case ButtonClicked(runButton) => run
    }


    def getMenuBar: MenuBar = {
      val menuBar = new MenuBar {

        contents += new Menu("SWhile") {

          contents += new MenuItem(new Action("About SWhile") {
            def apply = new Dialog {
              contents = Component.wrap(new JPanel() {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
                add(new JLabel(new ImageIcon("/Users/Lucas/IdeaProjects/ScalaPlayground/src/logo.png")) {
                  setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
                })
                add(new JLabel(" "))
                add(new JLabel("SWhile") {
                  setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
                  setFont(new Font("", java.awt.Font.BOLD, 16))
                })
                add(new JLabel("Version 0.2.0") {
                  setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
                })
                add(new JLabel(" "))
                add(new JLabel("Developed by Lucas Rijllart") {
                  setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
                })
                add(new JLabel("University of Sussex 2016") {
                  setAlignmentX(java.awt.Component.CENTER_ALIGNMENT)
                })
              })
              resizable = false
              preferredSize = new Dimension(280, 300)
              minimumSize = new Dimension(280, 300)
              setLocationRelativeTo(mainFrame)
              open()
            }
          })

          contents += new Separator

          contents += new MenuItem(new Action("Quit") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl Q"))
            def apply = { sys.exit() }
          })
        }


        contents += new Menu("File") {

          //New file

          contents += new MenuItem(new Action("New file") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl N"))

            def apply = {
              //get name popup
              val dialog = new Dialog {
                val fileNamePopupPane = new GridPanel(3, 1) {
                  val chooseNameLabel = new Label("Choose a name")
                  val fileNameTextField = new TextField() {
                    preferredSize = new swing.Dimension(100, 10)
                  }
                  val choiceButtons = new BoxPanel(Orientation.Horizontal) {
                    val fileNameCancelButton = new Button(new Action("Cancel") {
                      def apply = {
                        fileNameTextField.text = "" //set text to null
                        dispose() //dispose of window
                      }
                    })
                    val fileNameAcceptButton = new Button(new Action("Accept") {
                      def apply = {
                        val fileName = fileNameTextField.text
                        if (fileName.equals("")) {
                          //empty text box
                          fileNameTextField.border = BorderFactory.createLineBorder(Color.red)
                        } else if (listBuffer.contains(fileNameTextField.text)) {
                          chooseNameLabel.text = "Name cannot be empty!"
                          fileNameTextField.foreground = Color.red
                          chooseNameLabel.text = "Name already taken!"
                        } else {
                          fileNameTextField.text = ""
                          listView.listData = listBuffer += fileName //add file to list view
                          listView.selectIndices(listBuffer.indexOf(fileName)) //put selection on new file
                          val newFile = new WFile(fileName)
                          activeFile = newFile
                          activeFileList += newFile
                          codeTextArea.setEnabled(true)//enable text area
                          lineNumbers.setText("1")
                          codeTextArea.setText(activeFile.getContents)
                          dispose()
                        }
                      }
                    })
                    contents += fileNameCancelButton
                    contents += fileNameAcceptButton
                    defaultButton = fileNameAcceptButton
                  }
                  contents += chooseNameLabel
                  contents += fileNameTextField
                  contents += choiceButtons
                  preferredSize = new swing.Dimension(300, 100)
                }
                contents = fileNamePopupPane
                setLocationRelativeTo(mainFrame)
                listenTo(keys) //DOESNT WORK
                reactions += {
                  case KeyPressed(_, Key.Escape, _, _) => println("Hello")
                  case KeyPressed(_, Key.L, _, _) => println("L")
                }
              }
              dialog.open()
            }
          })


          //Open file

          contents += new MenuItem(a = new Action("Open file...") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl O"))

            def apply = {
              //make fileFilter
              val filter = new FileFilter {
                override def getDescription: String = "WHILE files"

                override def accept(f: File): Boolean = isAccepted(f)

                def isAccepted(f: File): Boolean = {
                  val path = f.getAbsolutePath.toLowerCase()
                  if (path.endsWith(".while") || path.endsWith(".txt") || f.isDirectory) {
                    true
                  } else {
                    false
                  }
                }
              }
              //make FileChooser
              val chooser = new FileChooser()
              chooser.fileFilter = filter
              chooser.title = title

              //if correct
              val result = chooser.showOpenDialog(null)
              if (result == FileChooser.Result.Approve) {
                Some(chooser.selectedFile)
                val filePath = chooser.selectedFile.toString //get file path

                //gather information
                var fileName = chooser.selectedFile.getName //get name of file
                if (fileName.endsWith(".txt")) {
                  //remove format
                  fileName = fileName.replace(".txt", "")
                } else if (fileName.endsWith(".while")) {
                  fileName = fileName.replace(".while", "")
                }

                if (listBuffer.contains(fileName)) {
                  println("File already there") //put this in console
                  listView.selectIndices(listBuffer.indexOf(fileName))
                } else {
                  //get file contents
                  var fileContents = ""
                  for (line <- scala.io.Source.fromFile(filePath).getLines()) {
                    fileContents += line + "\n"
                  }


                  //create new WFile with name and contents
                  val openedFile = new WFile(fileName, fileContents, filePath)

                  codeTextArea.setEnabled(false)
                  lineNumbers.setText("")
                  listBuffer += fileName //add file to list

                  listView.listData = listBuffer //add file to navigator

                  listView.selectIndices(listBuffer.indexOf(fileName))
                  activeFileList += openedFile
                  activeFile = openedFile
                  codeTextArea.setEnabled(true)
                  lineNumbers.setText("1")
                  codeTextArea.setText(fileContents)
                  codeTextArea.syntaxHighlightAll
                }
              }
            }
          })

          contents += new Separator()

          //Save
          contents += new MenuItem(new Action("Save") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl S"))

            def apply = {
              //check if file has a path
              println("Save")
              if (activeFile.getPath.equals("")) {
                saveAs()
              } else {
                save()
              }
            }
          })

          def save() = {
            println("saving...")
            println("File path:" + activeFile.getPath)
            val file = new File(activeFile.getPath) //get file from machine
            val out = new BufferedWriter(new FileWriter(file)) //get writer
            activeFile.setContents(codeTextArea.getText)
            println("Contents:\n" + activeFile.getContents)
            out.write(activeFile.getContents) //write contents to file
            out.close()
          }

          //Save as
          contents += new MenuItem(new Action("Save As...") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl shift S"))
            def apply = {
              saveAs()
            }
          })

          def saveAs() = {
            val saver = new FileChooser()
            saver.title = "Save " + activeFile.getName + " to..."
            saver.selectedFile = new File(activeFile.getName + ".while")
            val result = saver.showSaveDialog(mainFrame)
            if (result == FileChooser.Result.Approve) {
              activeFile.setPath(saver.selectedFile.toString)
              try {
                val writer = new BufferedWriter(new FileWriter(saver.selectedFile))
                writer.write(activeFile.getContents)
                writer.close()
              } catch {
                case e:Exception => JOptionPane.showMessageDialog(new JFrame(), e.getMessage, "Error", JOptionPane.ERROR_MESSAGE)
              }
            }
          }

          contents += new Separator

          contents += new MenuItem(new Action("Close file") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl W"))
            def apply = {
              val indexOfFile = listBuffer.indexOf(activeFile.getName)
              listBuffer.remove(indexOfFile) //remove file from listbuffer
              activeFileList.remove(activeFileList.indexOf(activeFile)) // from activeFileList
              if (listView.listData.nonEmpty) {
                val index = listBuffer.indexOf(activeFileList.head.getName)
                listView.selectIndices(index)
              } else {
                codeTextArea.setText("")
                lineNumbers.setText("")
                codeTextArea.setEnabled(false)
              }
            }
          })
        }

        // EDIT

        contents += new Menu("Edit") {
          contents += new MenuItem(new Action("Undo") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl Z"))
            def apply = {
              if (undo.canUndo) undo.undo()
            }
          })
          contents += new MenuItem(new Action("Redo") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl Y"))
            def apply = {
              if (undo.canRedo) undo.redo()
            }
          })
          contents += new Separator
          contents += new MenuItem(new Action("Copy") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl C"))
            def apply = {
              codeTextArea.copy()
            }
          })
          contents += new MenuItem(new Action("Cut") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl X"))
            def apply = {
              codeTextArea.cut()
            }
          })
          contents += new MenuItem(new Action("Paste") {
            accelerator = Some(KeyStroke.getKeyStroke("ctrl V"))
            def apply = {
              codeTextArea.paste()
            }
          })
        }

        // VIEW

        contents += new Menu("View") {
          contents += new MenuItem(new Action("Toggle navigation") {
            def apply = {
              if (mainFrame.dividerLocation.equals(0)) {
                mainFrame.dividerLocation = 100
              } else {
                mainFrame.dividerLocation = 0
              }
            }
          })
          contents += new MenuItem(new Action("Toggle console") {
            def apply = {
              if (consolePanel.visible) {
                consolePanel.visible = false
              } else {
                consolePanel.visible = true
              }
            }
          })
          //toggle I/O
          //toggle console?
        }

        // REFACTOR

        contents += new Menu("Refactor") {
          contents += new MenuItem(new Action("Rename variable") {
            def apply = new Dialog {
              val frame = new GridPanel(4,1) {
                contents += new Label("Rename variable:")
                val oldVar = new TextField()
                contents += oldVar
                val newVar = new TextField()
                contents += new BoxPanel(Orientation.Horizontal) {
                  contents += new Label("To:")
                  contents += newVar
                }
                contents += new BoxPanel(Orientation.Horizontal) {
                  contents += new Button(new Action("Cancel") {
                    def apply = {
                      close()
                    }
                  })
                  contents += new Button(new Action("Replace all") {
                    def apply = {
                      var text = codeTextArea.getText
                      text = text.replaceAll(oldVar.text, newVar.text)
                      codeTextArea.setText(text)
                      close()
                    }
                  })
                }
              }
              setLocationRelativeTo(mainFrame)
              contents = frame
              open()
            }
          })
        }

        //RUN


        contents += new Menu("Run") {
          //RUN CODE
          contents += new MenuItem(new Action("Run") {
            def apply = {
              run
            }
          })

          //DEBUGGER
          contents += new MenuItem(new Action("Debug") {
            var howManyLines = 0
            var previousLine = 0
            var previousLineLength = 0
            def apply = {
              println("Debug selected")
              val prog = codeTextArea.getText
              println("prog:\n" + prog)
              val input = inputArea.text
              println("input: " + input)

              println("Created src.interpreter")
              //create window

              new Dialog {
                val frame = new GridPanel(1, 2) {
                  val debuggerConsole = new TextArea() {

                  }
                  contents += new BoxPanel(Orientation.Vertical) {
                    val nextButton = new Button(new Action("Next Step") {
                      def apply = {
                        println("\nPressed next")
                        howManyLines += 1
                        runDebugger(howManyLines)
                        /*
                        val line = codeTextArea.getText.split("\n")(howManyLines)
                        println("Index:" + codeTextArea.getText.indexOf(line, previousLine))
                        val index = codeTextArea.getText.indexOf(line)
                        doc.setCharacterAttributes(previousLine, previousLineLength, codeTextArea.getStyle("Normal"), true)
                        doc.setCharacterAttributes(index, line.length, codeTextArea.getStyle("BreakLine"), true)
                        previousLine = index
                        previousLineLength = line.length
                        */
                      }
                    })
                    contents += nextButton
                    contents += new Button(new Action("Restart") {
                      def apply = {
                        howManyLines = 0
                        nextButton.enabled = true
                        runDebugger(howManyLines)
                      }
                    })
                    var varTable = new Table(10, 2) {
                      selection.elementMode = Table.ElementMode.Cell
                      showGrid = true
                      enabled = false
                    }
                    var varTableModel = varTable.model
                    contents += varTable

                    howManyLines = 0 //set initial line as 0. Could set this with a breakpoint

                    runDebugger(howManyLines)
                    /*
                    val line = codeTextArea.getText.replace("}", "").replace(";","").split("\n")
                    println("Lines:")
                    for (l <- line) {
                      println(l)
                    }
                    println("---- End of lines")

                    println("Index:" + codeTextArea.getText.indexOf(line))
                    val index = codeTextArea.getText.indexOf(line)
                    doc.setCharacterAttributes(index, line.length, codeTextArea.getStyle("BreakLine"), true)
                    previousLine = index
                    previousLineLength = line.length
*/
                    def runDebugger(shouldVisit: Int) {
                      //create debugger and run with amount of lines
                      val programParser = new ProgramParser(prog, input) //make new programParser
                      programParser.debugProgram(howManyLines) //run debugger

                      //get values from symbolTable
                      val symbolTable = programParser.debugger.symbolTable
                      var iterator = 0
                      for (key <- symbolTable.keySet().toArray.reverse) {
                        varTableModel.setValueAt(key, iterator, 0)
                        varTableModel.setValueAt(symbolTable.get(key).dataToString(), iterator, 1)
                        iterator += 1
                      }
                      debuggerConsole.text = programParser.debugger.fine
                      if (programParser.debugger.finished) {
                        nextButton.enabled = false
                      }
                    }
                  }
                  //contents += new FlowPanel() {
                    //contents += debuggerConsole
                  //}
                }
                setLocationRelativeTo(mainFrame)
                contents = frame
                open()
              }

            }

          })
        }

        contents += new Menu("Tools") {
          contents += new MenuItem(new Action("Translate code to data") {
            def apply = new Dialog {
              var result: String = ""
              try {
                result = programtodata.ProgramToData.run(codeTextArea.getText)
              } catch {
                case e: Exception => JOptionPane.showMessageDialog(new JFrame(), e.getMessage, "Error", JOptionPane.ERROR_MESSAGE)
              }
              val frame = new BoxPanel(Orientation.Vertical) {
                contents += new BoxPanel(Orientation.Horizontal) {
                  contents += new Button(new Action("Close") {
                    def apply = {
                      close()
                    }
                  })
                  contents += new Button(new Action("Copy") {
                    def apply = {
                      try {
                        val clipboard = java.awt.Toolkit.getDefaultToolkit.getSystemClipboard
                        val sel = new StringSelection(result)
                        clipboard.setContents(sel, sel)
                      } catch {
                        case e: Exception => JOptionPane.showMessageDialog(new JFrame(), e.getMessage, "Error", JOptionPane.ERROR_MESSAGE)
                      }
                    }
                  })
                }
                contents += new ScrollPane(new TextArea {
                  font = new Font("Monospace", 0 , 12)
                  tabSize = 2
                  rows = 40
                  columns = 80
                  editable = false
                  text = result
                })
              }
              title = activeFile.getName + " in data representation"
              contents = frame
              setLocationRelativeTo(mainFrame)
              open()
            }
          })
          contents += new MenuItem(new Action("Translate data to code") {
            def apply = new Dialog {
              var result = ""
              val codeTextArea = new TextArea {
                font = new Font("Monospaced", 0, 12)
                tabSize = 2
                rows = 30
                columns = 30
                editable = false
              }
              val dataTextArea = new TextArea {
                font = new Font("Monospaced", 0, 12)
                tabSize = 2
                rows = 30
                columns = 30
                lineWrap = true
              }
              val frame = new GridPanel(1, 2) {
                val dataPanel = new BoxPanel(Orientation.Vertical) {
                  contents += new Label("Enter program-as-data")
                  contents += new ScrollPane(dataTextArea)
                  contents += new BoxPanel(Orientation.Horizontal) {
                    contents += new Button(new Action("Close") {
                      def apply = {
                        close()
                      }
                    })
                    val copyButton = new Button(new Action("Copy") {
                      def apply = {
                        try {
                          val clipboard = java.awt.Toolkit.getDefaultToolkit.getSystemClipboard
                          val sel = new StringSelection(result)
                          clipboard.setContents(sel, sel)
                        } catch {
                          case e: Exception => JOptionPane.showMessageDialog(new JFrame(), e.getMessage, "Error", JOptionPane.ERROR_MESSAGE)
                        }
                      }
                    }) {
                      enabled = false
                    }
                    contents += copyButton
                    contents += new Button(new Action("Translate") {
                      def apply = {
                        //DO DATA TO CODE
                        result = DataToProgram.run(dataTextArea.text)
                        codeTextArea.text = result
                        copyButton.enabled = true
                      }
                    })
                  }
                }
                val codePanel = new BoxPanel(Orientation.Vertical) {
                  contents += new Label("Program:")
                  contents += new ScrollPane(codeTextArea)
                }
                contents += dataPanel
                contents += codePanel
              }
              contents = frame
              title = "Translate a program-as-data into a program"
              setLocationRelativeTo(mainFrame)
              open()
            }
          })

          contents += new Separator
          contents += new MenuItem(new Action("Parse program") {
            def apply = {
              val prog = codeTextArea.getText //get code
              val input = inputArea.text //get input
              try {
                new ProgramParser(prog, input).parseProgram()
                console.text += "Parsed " + activeFile.getName + " with no errors.\n"
              } catch {
                case parseError: src.interpreter.ParseException =>
                  JOptionPane.showMessageDialog(new JFrame(), parseError.getMessage, "Syntax error", JOptionPane.ERROR_MESSAGE)

                case e: Exception =>
                  JOptionPane.showMessageDialog(new JFrame(), e.getMessage, "Error", JOptionPane.ERROR_MESSAGE)

              }
            }
          })
          contents += new MenuItem(new Action("Write data under program") {
            def apply = {
              //get code from codeTextArea
              try {
                val result = programtodata.ProgramToData.run(codeTextArea.getText)
                codeTextArea.setText(codeTextArea.getText + "\n(*\n" + result + "*)")
              } catch {
                case e: Exception => JOptionPane.showMessageDialog(new JFrame(), e.getMessage, "Error", JOptionPane.ERROR_MESSAGE)
              }
            }
          })
          contents += new Separator
          contents += new MenuItem(new Action("Search...") {
            def apply = new Dialog {
              var offset = 0
              val searchTextField = new TextField {
                preferredSize = new Dimension(150, 10)
              }
              val label = new Label("")
              val searchButton = new Button(new Action("Search") {
                def apply = {
                  if (!searchTextField.text.isEmpty) {
                    searchTextField.border = BorderFactory.createLineBorder(Color.black)
                    val searchWord = searchTextField.text
                    if (codeTextArea.getText.contains(searchWord)) {
                      offset = 0
                      //println("Offset before search:" + offset)
                      offset = codeTextArea.getText.indexOf(searchWord, offset)
                      //println("Offset after search:" + offset)
                      val textInLines = codeTextArea.getText.split("\n")
                      val resultLine = doc.getDefaultRootElement.getElementIndex(offset) + 1
                      val resultRow = textInLines(resultLine).indexOf(searchWord, offset) + 1
                      label.text = "Found at line " + resultLine + " row " + resultRow
                      //codeTextArea.select(offset, offset + searchWord.length)
                      codeTextArea.setSelectionStart(offset)
                      codeTextArea.setSelectionEnd(offset + searchWord.length)
                      offset += searchWord.length //add length to offset, to look for words after
                      nextButton.enabled = true
                    } else {
                      label.text = "Not found!"
                    }
                  } else {
                    searchTextField.border = BorderFactory.createLineBorder(Color.red)
                  }
                }
              })
              val nextButton = new Button(new Action("Next") {
                enabled = false
                def apply = {
                  val searchWord = searchTextField.text
                  if (codeTextArea.getText.substring(offset).contains(searchWord)) {
                    println("Offset before next:" + offset)
                    println("Text after offset:" + codeTextArea.getText.substring(offset))
                    offset = codeTextArea.getText.indexOf(searchWord, offset)
                    println("Offset after next:" + offset)
                    val textInLines = codeTextArea.getText.split("\n")
                    println("lines:" + textInLines.length)
                    val resultLine = doc.getDefaultRootElement.getElementIndex(offset) + 1
                    val resultRow = textInLines(resultLine).indexOf(searchWord, offset) + 1
                    label.text = "Found at line " + resultLine + " row " + resultRow
                    //codeTextArea.select(offset, offset + searchWord.length)
                    codeTextArea.setSelectionStart(offset)
                    codeTextArea.setSelectionEnd(offset + searchWord.length)
                    offset += searchWord.length
                  } else {
                    offset = 0
                    apply
                  }
                }
              })
              val frame = new GridPanel(2,1) {
                contents += new BoxPanel(Orientation.Horizontal) {
                  contents += searchTextField
                  contents += new GridPanel(2,1) {
                    contents += searchButton
                    contents += nextButton
                  }
                }
                contents += label
              }
              modal = true
              contents = frame
              setLocationRelativeTo(mainFrame)
              open()
              listenTo(searchTextField)
              reactions += {
                case EditDone(`searchTextField`) => nextButton.enabled = false

              }
            }
          })
        }

        contents += new Menu("Help") {
          contents += new MenuItem(new Action("Quick start") {
            def apply = new Dialog {
              val text = "Welcome to SWhile, here is a quick guide to get started.\n" +
                "1. To create a new file go to File -> New file\n" +
                "2. Write your WHILE program in the text editor\n" +
                "3. Write an input in the input box and click 'Run'\n" +
                "4. The ouput is displayed in the output box\n"
              contents = new BoxPanel(Orientation.Vertical) {
                contents += new TextArea(text) {
                  editable = false
                  background = Color.white
                  font = new Font("", 0, 16)
                }
                contents += new Button(new Action("Close") {
                  def apply = { close() }
                })
              }
              background = Color.white
              setLocationRelativeTo(mainFrame)
              open()
            }
          })
          contents += new MenuItem("Manual")

          contents += new Separator

          contents += new MenuItem(new Action("Core grammar") {
            def apply = new Dialog {
              val grammar = "\t\tWHILE grammar\n\n" +
                "<expression>\t::=\t\tnil\n" +
                "\t\t\t\t  |\t\t<var>\n" +
                "\t\t\t\t  |\t\tcons <expr> <expr>\n" +
                "\t\t\t\t  |\t\thd <expr>\n" +
                "\t\t\t\t  |\t\ttl <expr>\n" +
                "\n" +
                "<var>\t\t\t::=\t\t'[A-Za-z]([A-Za-z0-9_-$.])'\n" +
                "\n" +
                "<block>\t\t\t::=\t\t{ }\n" +
                "\t\t\t\t  |\t\t{ <statements> }\n" +
                "\n" +
                "<statements>\t::=\t\t<cmd>\n" +
                "\t\t\t\t  |\t\t<cmd>; <statements>\n" +
                "\n" +
                "<cmd>\t\t\t::=\t\t<var> := <expr>\n" +
                "\t\t\t\t  |\t\twhile <expr> <block>\n" +
                "\t\t\t\t  |\t\tif <expr> <block> <elseblock>\n" +
                "\n" +
                "<elseblock>\t\t::=\n" +
                "\t\t\t\t  |\t\telse <block>\n" +
                "\n" +
                "<program>\t\t::=\t\t<name> read <var>\n" +
                "\t\t\t\t  |\t\t<block>\n" +
                "\t\t\t\t  |\t\twrite <var>\n" +
                "\n" +
                "<name>\t\t\t::=\t\t<var>"
              contents = new ScrollPane(new TextArea {
                text = grammar
                tabSize = 4
                preferredSize = new Dimension(425, 476)
                font = new Font("Monospaced", 0, 14)
                editable = false
              })
              background = Color.white
              setLocationRelativeTo(mainFrame)
              open()
            }
          })

          contents += new MenuItem(new Action("Extended grammar") {
            def apply = new Dialog {
              val grammar = "\t\tWHILE grammar\n\n" +
                "<expression>\t::=\t\tnil\n" +
                "\t\t\t\t  |\t\t<var>\n" +
                "\t\t\t\t  |\t\tcons <expr> <expr>\n" +
                "\t\t\t\t  |\t\thd <expr>\n" +
                "\t\t\t\t  |\t\ttl <expr>\n" +
                "\t\t\t\t  |\t\t<number>\n" +
                "\t\t\t\t  |\t\ttrue | false\n" +
                "\t\t\t\t  |\t\t[] | [<expr-list>]\n" +
                "\t\t\t\t  |\t\t<atoms>\n" +
                "\t\t\t\t  |\t\t<expr> = <expr>\n" +
                "\n" +
                "<var>\t\t\t::=\t\t[A-Za-z]([A-Za-z0-9_-$.])*\n" +
                "\n" +
                "<number>\t\t::=\t\t[0-9]\n" +
                "\n" +
                "<expr-list>\t\t::=\t\t<expr>\n" +
                "\t\t\t\t  |\t\t<expr>, <expr-list>\n" +
                "\n" +
                "<atoms>\t\t\t::=\t\tquote|var|cons|hd|tl|:=|while|if\n" +
                "\n" +
                "<block>\t\t\t::=\t\t{ }\n" +
                "\t\t\t\t  |\t\t{ <statements> }\n" +
                "\n" +
                "<statements>\t::=\t\t<cmd>\n" +
                "\t\t\t\t  |\t\t<cmd>; <statements>\n" +
                "\n" +
                "<cmd>\t\t\t::=\t\t<var> := <expr>\n" +
                "\t\t\t\t  |\t\twhile <expr> <block>\n" +
                "\t\t\t\t  |\t\tif <expr> <block> <elseblock>\n" +
                "\t\t\t\t  |\t\t<var> := < <name> > <expr>\n" +
                "\t\t\t\t  |\t\tswitch <expr> { <rule-list> }\n" +
                "\t\t\t\t  |\t\tswitch <expr> { <rule-list>\n" +
                "\t\t\t\t   \t\tdefault: <statements> }\n" +
                "\n" +
                "<rule-list>\t\t::=\t\t<rule>\n" +
                "\t\t\t\t  |\t\t<rule> <rule-list>\n" +
                "\n" +
                "<rule>\t\t\t::=\t\tcase <expr-list>: <statements>\n" +
                "\n" +
                "<elseblock>\t\t::=\n" +
                "\t\t\t\t  |\t\telse <block>\n" +
                "\n" +
                "<name>\t\t\t::=\t\t<var>\n" +
                "\n" +
                "<program>\t\t::=\t\t<name> read <var>\n" +
                "\t\t\t\t  |\t\t<block>\n" +
                "\t\t\t\t  |\t\twrite <var>"
              contents = new ScrollPane(new TextArea {
                text = grammar
                tabSize = 4
                preferredSize = new Dimension(394, 736)
                font = new Font("Monospaced", 0, 12)
                editable = false
              })
              background = Color.white
              setLocationRelativeTo(mainFrame)
              open()
            }
          })
        }
      }
      menuBar
    }

    def run = {
      console.text = ""
      var prog = codeTextArea.getText //get code
      var input = inputArea.text //get input

      //remove all simplified macro calls
      if (prog.contains("<")) {
        var iterator = 0
        while (prog.indexOf("<", iterator) > 0) { //while there still are "<" in prog
          val openBracket = prog.indexOf("<", iterator) //get opening bracket index
          iterator = openBracket
          val closeBracket = prog.indexOf(">", iterator) //get closing bracket index
          iterator = closeBracket
          val macroProgram = prog.substring(openBracket + 1, closeBracket) //get macro call
          var foundProgram = false
          for (file <- activeFileList) {
            if (file.getName == macroProgram) {
              foundProgram = true
              prog = prog.replaceAll(macroProgram, file.getPath) //replace macro call
            }
          }
          if (!foundProgram) {
            JOptionPane.showMessageDialog(new JFrame(), "Cannot find program '" + macroProgram + "' to macro call", "Macro error", JOptionPane.WARNING_MESSAGE)
          }
        }
      }

      //check if input is a program
      var foundProgram = false
      for (file <- activeFileList) {
        if (file.getName == input) {
          foundProgram = true
          try {
            val programCode = file.getContents
            input = programtodata.ProgramToData.run(programCode)
            console.text += "Using program " + file.getName + " as input.\n"
          } catch {
            case e: Exception => JOptionPane.showMessageDialog(new JFrame(), e, "Error", JOptionPane.ERROR_MESSAGE)
          }
        }
      }

      try {
        val startTime = System.currentTimeMillis()
        val interpreter = new ProgramParser(prog, input)
        val result: String = interpreter.runProgram().dataToString()
        outputTextField.text = result
        console.text += "Ran " + activeFile.getName + " with no errors.\n"

      } catch {
        case syntaxError: src.interpreter.ParseException =>
          JOptionPane.showMessageDialog(new JFrame(), syntaxError.getMessage, "Syntax error", JOptionPane.ERROR_MESSAGE)
          console.text += "Syntax error" + "\n"
          console.text += syntaxError.getMessage
        case e: InterpreterException =>
          if (e.getError == "Output var") {
            JOptionPane.showMessageDialog(new JFrame(), e.getErrorMessage, "Interpreter error", JOptionPane.WARNING_MESSAGE)
          } else {
            JOptionPane.showMessageDialog(new JFrame(), e.getErrorMessage, "Interpreter error", JOptionPane.ERROR_MESSAGE)
            console.text += "Error: " + e.getError + "\n"
            console.text += e.getErrorMessage
          }
        case e: Exception =>
          JOptionPane.showMessageDialog(new JFrame(), e, "Error", JOptionPane.ERROR_MESSAGE)
          console.text += e + "\n"
      }

    }
  }
}

class WFile(var name: String, var contents: String, var path: String) {

  /*
   *  First constructor with only filename
   *  Used when selecting "New File"
   */
  def this(name: String) {
    this(name, "", "")
  }

  def getName = this.name
  def getContents = this.contents
  def getPath = this.path

  def setName(newName: String) = this.name = newName
  def setContents(newContents: String) = this.contents = newContents
  def setPath(newPath: String) = this.path = newPath

}
