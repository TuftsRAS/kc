var mysql = require('mysql');
var fs = require('fs');
var parseXMLString = require('xml2js').parseString;

var kcConfig = require('os').homedir() + '/kuali/main/dev/kc-config.xml';
var conn = null;

fs.readFile(kcConfig, 'utf8', function (err, data) {
  parseXMLString(data, function(err, result) {
    var url = result.config.param.filter(o => o['$'].name == 'datasource.url')[0]['_'];
    var username = result.config.param.filter(o => o['$'].name == 'datasource.username')[0]['_'];
    var password = result.config.param.filter(o => o['$'].name == 'datasource.password')[0]['_'];
    var db = url.split('/').pop();

    conn = mysql.createConnection({
      host: 'localhost',
      database: db,
      user: username,
      password: password
    });

    conn.connect(function(err) {
      if (err) throw err;

      conn.query('SELECT * FROM sponsor_form_templates WHERE SPONSOR_FORM_ID = 1 AND PAGE_NUMBER IN (2, 3, 4, 5, 6, 7);', function (err, rows, fields) {
        if (err) throw err;

        rows.forEach(function (row) {
          var template = row.FORM_TEMPLATE.replace(/'/g, "''");

          console.log('DELETE FROM SPONSOR_FORM_TEMPLATES WHERE SPONSOR_FORM_ID = 1 AND PAGE_NUMBER = %d;', row.PAGE_NUMBER);
          console.log("INSERT INTO SPONSOR_FORM_TEMPLATES (SPONSOR_FORM_TEMPLATE_ID,SPONSOR_FORM_ID,PAGE_NUMBER,PAGE_DESCRIPTION,FILE_NAME,CONTENT_TYPE,FORM_TEMPLATE,UPDATE_USER,UPDATE_TIMESTAMP,OBJ_ID,VER_NBR) VALUES (SEQ_SPONSOR_FORM_TEMPLATES.NEXTVAL,(SELECT SPONSOR_FORM_ID FROM SPONSOR_FORMS WHERE SPONSOR_CODE='009800' and PACKAGE_NUMBER=2),%d,'%s','%s','text/xml',EMPTY_CLOB(),'admin',SYSDATE,SYS_GUID(),1);", row.PAGE_NUMBER, row.PAGE_DESCRIPTION, row.FILE_NAME);
          console.log('');
          for (var i = 0; i < (template.length / 30000); i++) {
            var buffer = template.substring(i * 30000, (i + 1) * 30000);

            console.log('DECLARE data CLOB; buffer VARCHAR2(30000);');
            console.log('BEGIN');
            console.log(' SELECT FORM_TEMPLATE INTO data FROM SPONSOR_FORM_TEMPLATES WHERE SPONSOR_FORM_ID = 1 AND PAGE_NUMBER = %d FOR UPDATE;', row.PAGE_NUMBER);
            console.log(" buffer := '%s';", buffer);
            console.log(' DBMS_LOB.writeappend(data,LENGTH(buffer),buffer);')
            console.log('END;');
            console.log('/');
            console.log('');
          }
        });

        process.exit();
      });
    });
  });
});
