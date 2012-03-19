#include <boost/lambda/lambda.hpp>
#include <iostream>
#include <iterator>
#include <algorithm>
#include <boost/algorithm/string.hpp>
#include <string>
#include <vector>
#include <fstream>

using namespace std;
using namespace boost;

int main(int argc, char** argv)
{

  string line;

  ifstream file(argv[1]);
  if(file.is_open()){
    while(file.good()){
      getline(file, line);
      cout << endl << line;
    }
    file.close();
  }
  
  string s1 = "dude how are ";

  typedef vector< string > split_vector_type;
    
  split_vector_type SplitVec; // #2: Search for tokens
  split( SplitVec, s1, is_any_of(" "), token_compress_on ); // SplitVec == { "hello abc","ABC","aBc goodbye" }
  
  for(split_vector_type::iterator i = SplitVec.begin(); i != SplitVec.end(); i++){
    cout << endl << "value : " << *i;
  }

  return 0;
}
