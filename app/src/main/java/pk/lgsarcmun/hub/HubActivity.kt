package pk.lgsarcmun.hub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import org.json.JSONObject

class HubActivity:ComponentActivity(){private val cameraPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){};override fun onCreate(s:Bundle?){super.onCreate(s);setContent{HubApp{cameraPermission.launch(Manifest.permission.CAMERA)}}}}
@Composable fun HubApp(requestCamera:()->Unit){var page by remember{mutableStateOf("home")};MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFF60A5FA),background=Color(0xFF030712),surface=Color(0xFF101827))){Scaffold(containerColor=Color(0xFF030712),bottomBar={NavigationBar(containerColor=Color(0xFF07111F)){listOf("home" to "Home","scan" to "Scan","member" to "Membership","more" to "More").forEach{(id,label)->NavigationBarItem(selected=page==id,onClick={page=id},icon={Text(label.first().toString(),fontWeight=FontWeight.Bold)},label={Text(label)})}}}){p->Box(Modifier.padding(p).fillMaxSize()){when(page){"home"->HomePage{page="scan"};"scan"->ScanPage(requestCamera);"member"->MembershipPage{page="auth"};"auth"->AuthPage{page="member"};else->MorePage()}}}}}
@Composable fun BrandHeader(){Column(Modifier.padding(24.dp)){Text("LGSARCMUN",color=Color.White,fontSize=13.sp,fontWeight=FontWeight.Bold,letterSpacing=3.sp);Text("Hub",color=Color(0xFF60A5FA),fontSize=40.sp,fontWeight=FontWeight.Black);Text("Official membership platform",color=Color(0xFF94A3B8),fontSize=14.sp)}}
@Composable fun GlassCard(m:Modifier=Modifier,c:@Composable ColumnScope.()->Unit){Card(m,shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF0D1828)),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0x22FFFFFF))){Column(Modifier.padding(22.dp),content=c)}}
@Composable fun HomePage(open:()->Unit){Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B1D33),Color(0xFF030712))))){BrandHeader();GlassCard(Modifier.padding(20.dp).fillMaxWidth()){Text("Everything LGSARCMUN, in one place.",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Membership, identity and secure QR scanning.",color=Color(0xFFCBD5E1),modifier=Modifier.padding(top=8.dp));Button(onClick=open,modifier=Modifier.fillMaxWidth().padding(top=20.dp),shape=RoundedCornerShape(16.dp)){Text("SCAN MEMBERSHIP")}};Text("Quick access",color=Color.White,fontSize=18.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(22.dp));Row(Modifier.padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){Tile("Membership","Digital ID",Modifier.weight(1f));Tile("Announcements","Stay updated",Modifier.weight(1f))}}}
@Composable fun Tile(a:String,b:String,m:Modifier){GlassCard(m){Text(a,color=Color.White,fontWeight=FontWeight.Bold);Text(b,color=Color(0xFF94A3B8),fontSize=12.sp,modifier=Modifier.padding(top=5.dp))}}
@Composable fun ScanPage(request:()->Unit){val context=LocalContext.current;val granted=ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;var result by remember{mutableStateOf<String?>(null)};Column(Modifier.fillMaxSize().background(Color(0xFF030712)),horizontalAlignment=Alignment.CenterHorizontally){Text("QR Scanner",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(top=25.dp));Text("Scan a membership QR code",color=Color(0xFF94A3B8));if(granted){QrScanner(Modifier.padding(20.dp).fillMaxWidth().aspectRatio(1f)){result=it};result?.let{ResultCard(it)}}else{Spacer(Modifier.height(60.dp));Button(onClick=request){Text("ENABLE CAMERA")}}}}
@Composable fun ResultCard(raw:String){var text by remember(raw){mutableStateOf("Checking membership…")};LaunchedEffect(raw){try{val id=raw.substringAfterLast("/membership/").ifBlank{raw};val r=SupabaseApi().verify(id);text=when{!r.optBoolean("found",false)->"NOT FOUND";r.optString("status")=="active"->"VALID MEMBERSHIP";else->"MEMBERSHIP REVOKED"}}catch(e:Exception){text="CHECK FAILED"}};GlassCard(Modifier.padding(horizontal=20.dp).fillMaxWidth()){Text(text,color=if(text=="VALID MEMBERSHIP")Color(0xFF4ADE80) else Color(0xFFF87171),fontSize=20.sp,fontWeight=FontWeight.Bold);Text(raw,color=Color(0xFF94A3B8),fontSize=12.sp,modifier=Modifier.padding(top=6.dp))}}
@Composable fun QrScanner(m:Modifier,onDetected:(String)->Unit){val context=LocalContext.current;var last by remember{mutableStateOf("")};AndroidView(modifier=m,factory={PreviewView(it).apply{scaleType=PreviewView.ScaleType.FILL_CENTER}},update={view->val f=ProcessCameraProvider.getInstance(context);f.addListener({val p=f.get();val preview=Preview.Builder().build().also{it.surfaceProvider=view.surfaceProvider};val scanner=BarcodeScanning.getClient();val analysis=ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();analysis.setAnalyzer(ContextCompat.getMainExecutor(context)){proxy->val media=proxy.image;if(media==null){proxy.close();return@setAnalyzer};scanner.process(InputImage.fromMediaImage(media,proxy.imageInfo.rotationDegrees)).addOnSuccessListener{codes->codes.firstOrNull{it.rawValue?.isNotBlank()==true}?.rawValue?.let{if(it!=last){last=it;onDetected(it)}}}.addOnCompleteListener{proxy.close()}};try{p.unbindAll();p.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner,CameraSelector.DEFAULT_BACK_CAMERA,preview,analysis)}catch(_:Exception){}},ContextCompat.getMainExecutor(context))})}
@Composable fun MembershipPage(open:()->Unit){Column(Modifier.fillMaxSize().background(Color(0xFF030712))){Text("Membership",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(24.dp));GlassCard(Modifier.padding(20.dp).fillMaxWidth()){Text("YOUR DIGITAL ID",color=Color(0xFF60A5FA),fontSize=11.sp,fontWeight=FontWeight.Bold);Text("Sign in to view your membership card.",color=Color.White,fontSize=19.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=10.dp));Button(onClick=open,modifier=Modifier.fillMaxWidth().padding(top=20.dp)){Text("SIGN IN / SIGN UP")}}}}
@Composable fun AuthPage(done:()->Unit){val scope=rememberCoroutineScope();var signup by remember{mutableStateOf(false)};var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};var status by remember{mutableStateOf("")};Column(Modifier.fillMaxSize().background(Color(0xFF030712)).padding(24.dp),verticalArrangement=Arrangement.Center){BrandHeader();GlassCard(Modifier.fillMaxWidth()){Text(if(signup)"Create account" else "Welcome back",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold);if(signup)OutlinedTextField(name,{name=it},label={Text("Full name")},modifier=Modifier.fillMaxWidth().padding(top=12.dp));OutlinedTextField(email,{email=it},label={Text("Email")},modifier=Modifier.fillMaxWidth().padding(top=12.dp));OutlinedTextField(password,{password=it},label={Text("Password")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth().padding(top=12.dp));if(status.isNotBlank())Text(status,color=Color(0xFFFCA5A5),modifier=Modifier.padding(top=10.dp));Button(onClick={scope.launch{try{if(signup)SupabaseApi().signUp(email,password,name) else SupabaseApi().signIn(email,password);done()}catch(e:Exception){status="Unable to authenticate. Check your details."}}},modifier=Modifier.fillMaxWidth().padding(top=18.dp)){Text(if(signup)"CREATE ACCOUNT" else "SIGN IN")};TextButton(onClick={signup=!signup},modifier=Modifier.align(Alignment.CenterHorizontally)){Text(if(signup)"Already have an account? Sign in" else "New member? Create an account")}}}}
@Composable fun MorePage(){Column(Modifier.fillMaxSize().background(Color(0xFF030712))){Text("More",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(24.dp));listOf("Announcements","Settings","Help & Support","About LGSARCMUN Hub").forEach{Text(it,color=Color.White,fontSize=17.sp,modifier=Modifier.fillMaxWidth().padding(20.dp))}}}
