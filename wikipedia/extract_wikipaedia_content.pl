use File::Basename;

$seed_file_path = $ARGV[0];

($name, $path, $suffix) = fileparse($seed_file_path);

open FILE, $seed_file_path;

while(<FILE>){
    $file_to_parse = $path . $_;
    print "\n processing $file_to_parse";
    `python extract_wikipaedia_content.py $file_to_parse`;
}

close(FILE);



