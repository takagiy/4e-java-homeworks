# 4EJプログラミングIII 課題１

## 要件

正九角形を描く

## 実行結果

![](http://localhost:8000/data/result.png)

## プロジェクト構成

タスクランナーのgradleをビルドツールとして使用して以下のディレクトリ構造でプロジェクトを構成した。

```
${dataFile('dir-tree')}
```

各ディレクトリの概要は以下のgradleの設定ファイルの通り。

${codeBlock('gradle', 'build.gradle')}

${codeBlock('gradle', 'settings.gradle')}

### サブプロジェクト `:markdown`

コードの改変、コメントの追加などを提出するpdfへ反映する工程を簡単にするため、プロジェクト内のコードをテンプレートとなるmarkdownファイルに埋め込むタスクを持ったサブプロジェクト `:markdown` を定義した。

テンプレート内で以下のように記述することでプロジェクト内の対応するコードを埋め込んだmarkdownファイルを生成する。

* このファイルを生成したテンプレートからの抜粋

```
各ディレクトリの概要は以下のgradleの設定ファイルの通り。

\${codeBlock('gradle', 'build.gradle')}

\${codeBlock('gradle', 'settings.gradle')}
```

## App

エントリポイントを含むソースコード。配布されたtgパッケージをラップしたライブラリを使用している。

${javaCode('app', 'app/DrawRegularPolygon.java')}

## Lib

tgをラップしたライブラリ。turtle graphicsにおいて、カメがどのように動くかを定義し、そのカメを画面上に出現させて動かす、という一連の流れがほとんどの場合に共通していると考えて、カメの動きを定める抽象メソッド`move`と、画面上にカメを出現させ定義した動作を実行させる`run`メソッドによって、turtle graphicsの基本の流れをユーザーに強制すると共に、宣言的で短いコードで実装できるようにした。また、あるカメの`move`メソッドの中で他のカメの`run`メソッドを呼ぶことで、複数の図形を合体させた図形を描くプログラムをわかりやすく実装できるようになることを意識した。

### `clone` vs copy constructor

Javaではプリミティブでないオブジェクトを複製する方法を提供する手段として`Object.clone`メソッドと`Clonable`インターフェースが用意されている。また、そのほかにコピーコンストラクタを実装するというやり方も広く使われている。
今回は、インスタンス化ができない抽象クラスに対しても実装が可能なコピーコンストラクタを用いた。

${javaCode('lib', 'turtle/graphics/Turtle.java')}

${javaCode('lib', 'turtle/graphics/Cartesian.java')}

${javaCode('lib', 'turtle/graphics/Screen.java')}
