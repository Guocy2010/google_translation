# google_translation
This project provides translation work for large mounts of words or place names from google translation or google map search.

To use this project, just import it into eclipse and export as runable jar. 

Usage info can be printed with the following command:

    java -jar your-export.jar

Usage:

    java -jar google_translate.jar file [options]

Options:

    -t|--thread      number of threads used to process
    -m|--method      0: google translation; 1: google mapsearch
    -s|--src_lang    this represents the source language of google translation,
                     and is only used when -m is 0. default is en
    -d|--dst_lang    this represents the destination language of google translation,
                     and is only used when -m is 0. default is zh-CN

The file you provided should be text formats, which use '\r\n','\n' or '\r' as line separator and '\t' as column separator. Each line could have multiple columns, but only the last column could be translated(by google translation) or searched(by google map). The output is a file named as the input-file's name suffixed with ".out".

Input file format example (input.txt):

    0	male	Jack
    1	female	Lucy
    2	male	Honey

Output file is (input.txt.out):

    0	male	Jack	插口
    1	female	Lucy	露西
    2	male	Honey	蜜糖