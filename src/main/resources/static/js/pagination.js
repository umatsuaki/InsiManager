
/*ページネーションでのエラー
 th:each="fileEntity : ${fileQueryResult}"> でfileEntityのBlobデータがjavascriptで認識できない

  th:href="@{/preview/{fileName}(id=${fileEntity.fid}, fileName=${fileEntity.fileName})}"
  @メソッドがjavascriptで処理できない

  [[${fileQueryResult.get(i).fid}]] などthymeleafで読み込んだ変数にiやjを代入できない

  javascriptの変数をhtmlに渡せない

*/

function preview(fileName, id) {
// リクエストを作成
var xhr = new XMLHttpRequest();
xhr.open('GET', '/preview/' + fileName + '?id=' + id, true);
xhr.responseType = 'blob';

// レスポンスがロードされたときの処理
xhr.onload = function() {
    if (this.status === 200) {
        // レスポンスを Blob として取得
        var blob = this.response;
        var url = URL.createObjectURL(blob);

        // プレビュー用の新しいウィンドウを開く
        var newWindow = window.open();
        newWindow.document.open();
        newWindow.document.write('<iframe src="' + url + '" width="100%" height="100%"></iframe>');
        newWindow.document.close();
    }
};

// リクエスト送信
xhr.send();
}

// ページ数を取得
var page = Number(getQueryParam('page'));
if (page < 1) page = 1;
var count = /*[[${resultSize}]]*/ "count";
perPage = 15;
maxPage = Math.ceil(count / perPage); 

// コンテンツ
var html = '<thead class="table-dark"> <th>ファイル名</th></thead>';
var i;
for (i = (page - 1) * perPage + 1; i <= page * perPage && i <= count; i++) {
    var j= i- (page-1) * perPage - 1;
    var fid = /*[[${fileQueryResult.get(3).fid}]]*/ "fid";
    var fName = /*[[${fileQueryResult.get(3).fileName}]]*/ "fName";
    html += `<tbody>
                    <tr>
                        <td>
                            <a href="#" onclick="preview('${fileQueryResult.get(0).fileName}', ${fileQueryResult.get(0).fid})">PDFプレビュー</a>
                        </td>
                    </tr>
                </tbody>`;
}
document.getElementById('container').innerHTML = html;

// ページネーション
html = '';
if (page > 1) {
    html += `<li class="page-item"><a class="page-link" href="?page=${page - 1}">前へ</a></li>`;
}
for (i = page - 2; i <= page + 2 && i <= maxPage; i++) {
    if (i < 1) continue;
    if (i == page) {
        html += `<li class="page-item active"><a class="page-link" href="?page=${i}">${i}</a></li>`;
        continue;
    }
    html += `<li class="page-item"><a class="page-link" href="?page=${i}">${i}</a></li>`;
}
if (page != maxPage) {
    html += `<li class="page-item"><a class="page-link" href="?page=${page + 1}">次へ</a></li>`
}
document.getElementById('pagination').innerHTML = html;

// URLから指定したパラメータを取得
function getQueryParam($key) {
    if (1 < document.location.search.length) {
        var query = document.location.search.substring(1);
        var parameters = query.split('&');
        for (var i = 0; i < parameters.length; i++) {
            // パラメータ名とパラメータ値に分割する
            var element = parameters[i].split('=');
            if (element[0] == $key) return element[1];
        }
    }
    return null;
}

